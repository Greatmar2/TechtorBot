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

package takeshi.util;


import java.io.Serializable;

/**
 * The type Pair.
 *
 * @param <K> the type parameter
 * @param <V> the type parameter
 */
public class Pair<K, V> implements Serializable {


    private K key;
    private V value;


	/**
	 * Instantiates a new Pair.
	 *
	 * @param key   the key
	 * @param value the value
	 */
	public Pair(K key, V value) {
        this.key = key;
        this.value = value;
    }

	/**
	 * Gets key.
	 *
	 * @return the key
	 */
	public K getKey() {
        return key;
    }

	/**
	 * Gets value.
	 *
	 * @return the value
	 */
	public V getValue() {
        return value;
    }

    @Override
    public String toString() {
        return key + "=" + value;
    }

    @Override
    public int hashCode() {
        return key.hashCode() * 13 + (value == null ? 0 : value.hashCode());
    }
}