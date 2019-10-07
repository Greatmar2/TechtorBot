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

package takeshi.db.model;

import takeshi.db.AbstractModel;

/**
 * The type O music.
 */
public class OMusic extends AbstractModel {
	/**
	 * The Id.
	 */
	public int id = 0;
	/**
	 * The Youtubecode.
	 */
	public String youtubecode = "";
	/**
	 * The Filename.
	 */
	public String filename = "";
	/**
	 * The Youtube title.
	 */
	public String youtubeTitle = "";
	/**
	 * The Artist.
	 */
	public String artist = "";
	/**
	 * The Lastplaydate.
	 */
	public long lastplaydate = 0;
	/**
	 * The Banned.
	 */
	public int banned = 0;
	/**
	 * The Title.
	 */
	public String title = "";
	/**
	 * The Play count.
	 */
	public int playCount = 0;
	/**
	 * The Last manual playdate.
	 */
	public long lastManualPlaydate = 0L;
	/**
	 * The File exists.
	 */
	public int fileExists = 1;
	/**
	 * The Duration.
	 */
	public int duration = 0;
	/**
	 * The Requested by.
	 */
	public String requestedBy = "";
}