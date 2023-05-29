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
import lct.feedbacksrv.resource.PyRequestObject;
import lct.feedbacksrv.resource.PyResponseObject;
import lct.feedbacksrv.resource.RestClient;
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

    private RestClient restClient;
    @PostConstruct
    private void postConstruct() {
        restClient = new RestClient(serviceHost, servicePort);
    }

    @Autowired
    MessageRepository messageRepository;
    @Autowired
    PartnerRepository partnerRepository;
    @Autowired
    PostamatRepository postamatRepository;
    @Autowired
    CategoryRepository categoryRepository;

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
    public Message addMessage(MessageUI message) {
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
            newMessage = messageRepository.saveAndFlush(newMessage);
            List<Message> singleList = new ArrayList<>();
            singleList.add(newMessage);
            analyzeMessages(List.of(newMessage));
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
                    log.info("body: {}", body);
                    JSONParser parser = new JSONParser();
                    JSONArray respData = (JSONArray) parser.parse((String) body.string());
                    log.info("Resp: {}", respData);
                    respList = objectMapper.readValue(respData.toString(),
                            new TypeReference<List<PyResponseObject>>() {});
                    log.info("respList: {}", respList);

                    if(!respList.isEmpty()) {
                        respList.forEach(pyResponseObject -> {
                            Message message = messageRepository.getById(pyResponseObject.getId());
                            log.info("respObj {}", pyResponseObject);
                            message.setTone(pyResponseObject.getTone_predicted());
                            if(pyResponseObject.getProblem_predicted() != null) {
                                String code = String.valueOf(pyResponseObject.getProblem_predicted());
                                log.info("code {}", code);
                                Optional<Category> c = categoryRepository
                                        .findByCode(String.valueOf(pyResponseObject.getProblem_predicted().intValue()))
                                        .stream()
                                        .findFirst();
                                c.ifPresent(message::setCategory);
                                parsed.add(messageRepository.saveAndFlush(message));
                            } else {
                                String code = "-";
                                log.info("code {}", code);
                                Optional<Category> c = categoryRepository
                                        .findByCode(code)
                                        .stream()
                                        .findFirst();
                                c.ifPresent(message::setCategory);
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
}
