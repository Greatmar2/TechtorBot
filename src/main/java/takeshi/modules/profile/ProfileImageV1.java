/*
 * Copyright 2017 github.com/kaaz
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

package takeshi.modules.profile;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import net.dv8tion.jda.api.entities.User;
import takeshi.main.Launcher;
import takeshi.util.GfxUtil;

/**
 * Created on 28-9-2016
 */
public class ProfileImageV1 extends ProfileImage {

	/**
	 * Instantiates a new Profile image v 1.
	 *
	 * @param user the user
	 */
	public ProfileImageV1(User user) {
		super(user);
	}

	/**
	 * Gets profile image.
	 *
	 * @return the profile image
	 * @throws IOException the io exception
	 */
	public File getProfileImage() throws IOException {
		Font defaultFont = new Font("Helvetica", Font.BOLD & Font.ITALIC, 36);
		Font creditFont = new Font("comissans", Font.ITALIC, 20);
		BufferedImage result = new BufferedImage(645, 265, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = (Graphics2D) result.getGraphics();
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		BufferedImage profileImg = getUserAvatar();
		BufferedImage backgroundImage = ImageIO
				.read(Launcher.class.getClassLoader().getResource("profile_bg_test_2.png"));

		g.drawImage(profileImg, 66, 30, 197, 155, 0, 0, profileImg.getWidth(), profileImg.getHeight(), null);
		g.drawImage(backgroundImage, 0, 0, 645, 265, 0, 0, 645, 265, null);

		GfxUtil.addCenterText(getUser().getName(), defaultFont, 125, 200, g, Color.black);
		GfxUtil.addText("made by Techtor", creditFont, 506, 260, g, new Color(0xFFE7FA));

		File file = new File("profile_v1_" + getUser().getId() + ".png");
		ImageIO.write(result, "png", file);
		return file;
	}
}
