package com.mentalfrostbyte.jello.module.wurst;

import com.mentalfrostbyte.jello.module.Category;
import com.mentalfrostbyte.jello.module.Module;

/**
 * Wurst InfiniChat: removes the 256 character limit from the chat input.
 *
 * <p>Not fully ported: needs a mixin on {@code ChatScreen.init()} (raise
 * {@code EditBox.setMaxLength}) and on {@code ChatScreen.normalizeChatMessage} (skip
 * {@code StringUtil.trimChatMessage} for commands). The module is kept as a toggle for that hook.</p>
 */
public class InfiniChat extends Module {
	public InfiniChat() {
		super("Infini Chat", 0, Category.MISC);
	}
}
