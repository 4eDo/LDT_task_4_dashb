package lct.feedbacksrv.service;

import lct.feedbacksrv.apiModels.MessageUI;
import lct.feedbacksrv.domain.Message;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Report service
 *
 * @author Maria Balashova (m.balashova@lar.tech)
 */
public interface FeedbackService {
   void initOnStart();
   Optional<Message> getMessage(Long id);
   Message messageUItoMessage(MessageUI message);
   List<Message> addMessages(List<MessageUI> messageUIS);
   List<Message> getAllMessages();
   List<Message> getMessagesByLimitAndOffset(int limit, int offset);

   Long getCount();

   void updCategory(Long id, Long newCategory, Boolean needTicket);

   Map<Long, String> getCategoriesForUi();
}
