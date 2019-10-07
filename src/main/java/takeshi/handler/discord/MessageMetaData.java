package takeshi.handler.discord;

/**
 * The type Message meta data.
 */
public class MessageMetaData {
    private final long channelId;
    private final long messageId;

	/**
	 * Instantiates a new Message meta data.
	 *
	 * @param channelId the channel id
	 * @param messageId the message id
	 */
	public MessageMetaData(long channelId, long messageId) {
        this.channelId = channelId;
        this.messageId = messageId;
    }

	/**
	 * Gets channel id.
	 *
	 * @return the channel id
	 */
	public long getChannelId() {
        return channelId;
    }

	/**
	 * Gets message id.
	 *
	 * @return the message id
	 */
	public long getMessageId() {
        return messageId;
    }
}
