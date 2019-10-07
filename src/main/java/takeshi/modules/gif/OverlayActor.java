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
 * The type Overlay actor.
 */
public class OverlayActor {
	/**
	 * The Visibility.
	 */
	public boolean visibility;
	/**
	 * The X.
	 */
	public int x, /**
	 * The Y.
	 */
	y;
	/**
	 * The Height.
	 */
	public int height, /**
	 * The Width.
	 */
	width;

	/**
	 * Instantiates a new Overlay actor.
	 */
	public OverlayActor() {
        visibility = true;
        x = 0;
        y = 0;
        width = 25;
        height = 25;
    }

	/**
	 * Apply.
	 *
	 * @param event the event
	 */
	public void apply(OverlayEvent event) {
        event.apply(this);
    }
}
