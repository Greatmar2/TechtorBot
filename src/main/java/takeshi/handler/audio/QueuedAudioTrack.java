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

package takeshi.handler.audio;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

/**
 * The type Queued audio track.
 */
public class QueuedAudioTrack {

    final private String userId;
    final private AudioTrack track;

	/**
	 * Instantiates a new Queued audio track.
	 *
	 * @param userId the user id
	 * @param track  the track
	 */
	public QueuedAudioTrack(String userId, AudioTrack track) {
        this.userId = userId;
        this.track = track;
    }

	/**
	 * Gets user id.
	 *
	 * @return the user id
	 */
	public String getUserId() {
        return userId;
    }

	/**
	 * Gets track.
	 *
	 * @return the track
	 */
	public AudioTrack getTrack() {
        return track;
    }
}
