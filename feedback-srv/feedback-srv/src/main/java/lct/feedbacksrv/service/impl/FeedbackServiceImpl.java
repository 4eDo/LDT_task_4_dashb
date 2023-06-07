package lct.feedbacksrv.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import lct.feedbacksrv.apiModels.MessageUI;
import lct.feedbacksrv.domain.Category;
import lct.feedbacksrv.domain.Message;
import lct.feedbacksrv.domain.Partner;
import lct.feedbacksrv.domain.Postamat;
import lct.feedbacksrv.repository.CategoryRepository;
import lct.feedbacksrv.repository.MessageRepository;
import lct.feedbacksrv.repository.PartnerRepository;
import lct.feedbacksrv.repository.PostamatRepository;
import lct.feedbacksrv.resource.AsyncParameters;
import lct.feedbacksrv.resource.PyRequestObject;
import lct.feedbacksrv.resource.PyResponseObject;
import lct.feedbacksrv.service.FeedbackService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static lct.feedbacksrv.resource.Utils.objectMapper;
import static lct.feedbacksrv.resource.Utils.stringToDate;

/**
 * TODO: Add class description
 *
 * @author Maria Balashova (m.balashova@lar.tech)
 */
@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class FeedbackServiceImpl implements FeedbackService {

    @Value("${analyse.service.host}")
    private String serviceHost;
    @Value("${analyse.service.port}")
    private int servicePort;
    @Value("${feedback.spam.sec}")
    private int spamSec;
    @Value("${feedback.statuses.recheck.sec}")
    private int recheckSec;

    @Autowired
    MessageRepository messageRepository;
    @Autowired
    PartnerRepository partnerRepository;
    @Autowired
    PostamatRepository postamatRepository;
    @Autowired
    CategoryRepository categoryRepository;

    private final Runtime runtime = Runtime.getRuntime();
    private final AsyncParameters asyncParameters;
    private final ThreadPoolExecutor executorService;
    private final ScheduledExecutorService scheduledExecutorService;


    @PostConstruct
    private void postConstruct() {
        showMemoryStat();
        initOnStart();
        checkStatusesPrimo();
        checkStatusesSecundo();
    }

    private void initOnStart() {
        try{
            log.info("~~~~~~~~ INIT ON START AND FIX BUGS ~~~~~~~");
            // Маркируем то, что было ранее.
            messageRepository.findMessagesWithoutStatus().forEach(message -> message.setStatus("UNKNOWN"));

            messageRepository.findMessagesByStatus("SET_RAND_DATA").forEach(message -> {
                message.setStatus("NEW_FROM_FILE");
                messageRepository.saveAndFlush(message);
            });
            messageRepository.findMessagesByStatus("CHECK_TONE").forEach(message -> {
                message.setStatus("NEW_FROM_UI");
                messageRepository.saveAndFlush(message);
            });
            messageRepository.findMessagesByStatus("CREATE_TICKET").forEach(message -> {
                message.setStatus("TONE_CHECKED");
                messageRepository.saveAndFlush(message);
            });

            log.info("~~~~~~~~ END INIT ON START AND FIX BUGS ~~~~~~~");
        } catch (Exception e) {
            log.error("Exceprion on init method: ", e);
        }
    }


    private void checkStatusesPrimo() {
        try {
            int activeTasks = executorService.getActiveCount();
            int maxTasks = asyncParameters.getPoolWorkerSize();
            int reserved = 1; // Зарезервировано для потока, сохраняющего файл в бд!
            int needed = maxTasks - activeTasks - reserved;
            int chunkSize = 1000;
            float free = calculate(runtime.maxMemory() - (runtime.totalMemory() - runtime.freeMemory()));
            boolean hasFreeMem = free > 60;
            if (needed > 0 && hasFreeMem) {
                List<Message> toCheckTone = new ArrayList<>();
                toCheckTone.addAll(messageRepository.findMessagesByStatusWithLimit("NEW_FROM_UI", chunkSize));
                toCheckTone.addAll(messageRepository.findMessagesByStatusWithLimit("SET_RAND_DATA_DONE", chunkSize - toCheckTone.size()));
                log.info("*** [To check tone] Got messages count: {}, chunkSize: {}", toCheckTone.size(), chunkSize);
                toCheckTone.forEach(message -> message.setStatus("CHECK_TONE"));
                messageRepository.saveAllAndFlush(toCheckTone);
                executorService.submit(() -> analyzeMessages(toCheckTone));
            }
        } catch (Exception e) {
            log.error("1 Exception on check messages status", e);
        }
        scheduledExecutorService.schedule(this::checkStatusesPrimo, recheckSec, TimeUnit.SECONDS);
    }

    private void checkStatusesSecundo() {
        try {
            int activeTasks = executorService.getActiveCount();
            int maxTasks = asyncParameters.getPoolWorkerSize();
            int reserved = 1; // Зарезервировано для потока, сохраняющего файл в бд!
            int needed = maxTasks - activeTasks - reserved;
            int chunkSize = 1000;
            float free = calculate(runtime.maxMemory() - (runtime.totalMemory() - runtime.freeMemory()));
            boolean hasFreeMem = free > 60;
            if (needed > 0 && hasFreeMem) {
                List<Message> toSetRandData = messageRepository.findMessagesByStatusWithLimit("NEW_FROM_FILE", chunkSize);
                log.info("*** [To set rand data] Got messages count: {}, chunkSize: {}", toSetRandData.size(), chunkSize);
                toSetRandData.forEach(message -> message.setStatus("SET_RAND_DATA"));
                messageRepository.saveAllAndFlush(toSetRandData);
                executorService.submit(() -> setRandData(toSetRandData));
            }
        } catch (Exception e) {
            log.error("2 Exception on check messages status", e);
        }
        scheduledExecutorService.schedule(this::checkStatusesSecundo, recheckSec, TimeUnit.SECONDS);
    }

    private void showMemoryStat() {
        try {
            long used = runtime.totalMemory() - runtime.freeMemory();
            long free = runtime.maxMemory() - used;
            log.info(String.format("######## Used: %,d (%.1f%%), Free: %,d (%.1f%%), Total: %,d ########",
                    used,
                    calculate(used),
                    free,
                    calculate(free),
                    runtime.maxMemory()));
            log.info("######## Thread max pool size: {}, Current pool queue: {}",
                    executorService.getMaximumPoolSize(), executorService.getActiveCount());
        } catch (Exception e) {
            log.error("Exception on show system memory", e);
        }
        scheduledExecutorService.schedule(this::showMemoryStat, spamSec, TimeUnit.SECONDS);
    }
    private float calculate(long targetMemory) {
        return (float) targetMemory / (float) runtime.maxMemory() * 100;
    }

    @Override
    public Optional<Message> getMessage(Long id) {
        try{
            return messageRepository.findById(id);
        } catch (Exception e) {
            log.error("Error in getMessage method", e);
        }
        return Optional.empty();
    }

    @Override
    public Message messageUItoMessage(MessageUI message) {
        try{
            if (message == null){
                return Message.builder().build();
            }
            Message.MessageBuilder m = Message.builder();
            if(message.hasUsername()) {
                m.username(message.getUsername());
            }
            if(message.hasCreateDate()) {
                Date d = stringToDate(message.getCreateDate());
                m.createDate(d);
            } else {
                m.createDate(new Date());
            }
            if(message.hasPostamat()) {
                Optional<Postamat> p = postamatRepository.findById(message.getPostamat());
                p.ifPresent(m::postamat);
            }
            if(message.hasOrderId()) { m.orderId(message.getOrderId()); }
            if(message.hasMessage()) { m.message(message.getMessage()); }
            if(message.hasStars()) { m.stars(message.getStars()); }
            if(message.hasPartner()) {
                Optional<Partner> p = partnerRepository.findById(message.getPartner());
                p.ifPresent(m::partner);
            }
            Message newMessage = m.build();
            newMessage.setStatus("NEW_FROM_UI");
            return messageRepository.saveAndFlush(newMessage);
        } catch (Exception e) {
            log.error("Error in addMessage method", e);
        }
        return null;
    }

    @Override
    public List<Message> addMessages(List<MessageUI> messageUIS) {
        List<Message> messages = new ArrayList<>();
        messageUIS.forEach(message -> {
            try{
                Message.MessageBuilder m = Message.builder();
                if(message.hasUsername()) {
                    m.username(message.getUsername());
                }
                if(message.hasCreateDate()) {
                    Date d = stringToDate(message.getCreateDate());
                    m.createDate(d);
                } else {
                    m.createDate(new Date());
                }
                if(message.hasPostamat()) {
                    Optional<Postamat> p = postamatRepository.findById(message.getPostamat());
                    p.ifPresent(m::postamat);
                }
                if(message.hasOrderId()) { m.orderId(message.getOrderId()); }
                if(message.hasMessage()) { m.message(message.getMessage()); }
                if(message.hasStars()) { m.stars(message.getStars()); }
                if(message.hasPartner()) {
                    Optional<Partner> p = partnerRepository.findById(message.getPartner());
                    p.ifPresent(m::partner);
                }
                Message newMessage = messageRepository.saveAndFlush(m.build());

                messages.add(newMessage);
            } catch (Exception e) {
                log.error("Error in addMessage method", e);
            }
        });
        List<Message> done = analyzeMessages(messages);
        return done;
    }


    @Override
    public List<Message> getAllMessages() {
        try{
            return messageRepository.findAll();
        } catch (Exception e) {
            log.error("Error in getAllMessages method", e);
        }
        return Collections.emptyList();
    }

    @Override
    public List<Message> getMessagesByLimitAndOffset(int limit, int offset) {
        try{
            return messageRepository.findPage(offset,limit);
        } catch (Exception e) {
            log.error("Error in getMessagesByLimitAndOffset method", e);
        }
        return Collections.emptyList();
    }

    @Override
    public Long getCount() {
        return messageRepository.count();
    }

    private List<Message> analyzeMessages(List<Message> messages) {
        if(messages.isEmpty()) return Collections.emptyList();
        List<Message> parsed = new ArrayList<>();
        List<PyRequestObject> pyRequestObjects = new ArrayList<>();
        messages.forEach(message -> {
            if(message.getMessage().isBlank()) {
                if(message.getStars() > 3) message.setTone(1f); // Positive
                if(message.getStars() == 3) message.setTone(2f);// Neutral
                if(message.getStars() < 3) message.setTone(3f); // Negative

    //         TODO   message.setCategory();
                parsed.add(message);
            } else {
                pyRequestObjects.add(PyRequestObject.builder()
                        .id(message.getId())
                        .comment(message.getMessage())
                        .build());
            }
        });

            String json = null;
            try {
                json = objectMapper.writeValueAsString(pyRequestObjects);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }

            List<PyResponseObject> respList = new ArrayList<>();

            Map<String, String> headers = new HashMap<>();
            headers.put("Content-Type", "application/json;charset=WINDOWS-1251");
            try{
                MediaType JSON = MediaType.parse("application/json; charset=utf-8");
                RequestBody requestBody = RequestBody.create(JSON, json);

                Request request = new Request.Builder()
                        .url(String.format("http://%s:%s/api/post/", serviceHost, servicePort))
                        .post(requestBody)
                        .build();
                OkHttpClient client = new OkHttpClient();
                try (Response response = client.newCall(request).execute()) {
                    ResponseBody body = response.body();
//                    log.info("body: {}", body);
                    JSONParser parser = new JSONParser();
                    JSONArray respData = (JSONArray) parser.parse((String) body.string());
//                    log.info("Resp: {}", respData);
                    respList = objectMapper.readValue(respData.toString(),
                            new TypeReference<List<PyResponseObject>>() {});
                    log.info("respList: {}", respList);

                    if(!respList.isEmpty()) {
                        respList.forEach(pyResponseObject -> {
                            Message message = messageRepository.findById(pyResponseObject.getId()).get();
                            log.info("respObj {}", pyResponseObject);
                            if(pyResponseObject.getProblem_predicted() != null) {
                                message.setTone(pyResponseObject.getTone_predicted());
                                String code = String.valueOf(pyResponseObject.getProblem_predicted());
                                log.info("code {}", code);
                                Optional<Category> c = categoryRepository
                                        .findByCode(String.valueOf(pyResponseObject.getProblem_predicted().intValue()))
                                        .stream()
                                        .findFirst();
                                c.ifPresent(message::setCategory);
                                message.setStatus("TONE_CHECKED");
                                parsed.add(messageRepository.saveAndFlush(message));
                            } else {
                                String code = "-";
                                log.info("code {}", code);
                                Optional<Category> c = categoryRepository
                                        .findByCode(code)
                                        .stream()
                                        .findFirst();
                                c.ifPresent(message::setCategory);
                                if(pyResponseObject.getTone_predicted() == null) {
                                    if(message.getStars() > 3) {
                                        message.setTone(1f);
                                    } else if(message.getStars() <3) {
                                        message.setTone(3f);
                                    } else {
                                        message.setTone(2f);
                                    }
                                }
                                message.setStatus("TONE_CHECKED");
                                parsed.add(messageRepository.saveAndFlush(message));
                            }
                        });
                    }
                }
            } catch (Exception ex) {
                log.error("error on analyse method: ", ex);
            }
        return parsed;
    }

    private void setRandData(List<Message> toSetRandData) {
        log.info("set rand data");
        if(toSetRandData.isEmpty()) return;
        List<Postamat> postamats = postamatRepository.findExists();
        List<Partner> partners = partnerRepository.findExists();
        toSetRandData.forEach(message -> {
                message.setPostamat(postamats.get(new Random().nextInt(postamats.size())));
                message.setPartner(partners.get(new Random().nextInt(partners.size())));
                message.setStatus("SET_RAND_DATA_DONE");
                messageRepository.saveAndFlush(message);
            }
        );
    }

}
