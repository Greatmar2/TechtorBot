/*
 * Copyright 2018 Greatmar2
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package takeshi.util;

import java.awt.Color;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;

public class QuickEmbedBuilder {
	// Default values
	public static final Color DEFAULT_COL = new Color(0, 255, 0);
	public static final Color ERROR_COL = Color.RED;
	public static final Color WARN_COL = Color.YELLOW;
	public static final Color PERM_COL = Color.BLUE;
	public static final Color ELEK_COL = new Color(46, 204, 113);
	public static final Color MAR_COL = new Color(0, 37, 173);

	// Converts a plain string into an embed
	public static MessageBuilder embedString(String message) {
		return embedStringColor(message, DEFAULT_COL);
	}

	// Converts a plain string into an embed, with a specific color
	public static MessageBuilder embedStringColor(String message, Color col) {
		EmbedBuilder embBuild = new EmbedBuilder();
		embBuild.setDescription(message);
		embBuild.setColor(col);

		return new MessageBuilder(embBuild);
	}
}
