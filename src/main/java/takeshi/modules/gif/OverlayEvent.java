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

package takeshi.modules.gif;

/**
 * The interface Overlay event.
 */
public interface OverlayEvent {

	/**
	 * Whats the trigger for this event?
	 *
	 * @return name of trigger
	 */
	String getTrigger();

	/**
	 * checks if the input is a valid one
	 *
	 * @param input the input
	 * @return valid ?
	 */
	boolean isValid(String input);

	/**
	 * Parse overlay event.
	 *
	 * @param input the input
	 * @return the overlay event
	 */
	OverlayEvent parse(String input);

	/**
	 * Apply the event to an actor
	 *
	 * @param overlayActor the actor
	 */
	void apply(OverlayActor overlayActor);
}
