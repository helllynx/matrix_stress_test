/*
 * matrix-java-sdk - Matrix Client SDK for Java
 * Copyright (C) 2018 Arne Augenstein
 *
 * https://www.kamax.io/
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package matrix.event;

import matrix.room.RoomHistoryVisibility;

public interface _RoomHistoryVisibilityEvent extends _RoomEvent {

    /**
     * There is an enum for handling the return values defined in th specification: {@link RoomHistoryVisibility}
     * 
     * @return The current setting of the room for the visibility of future messages.
     */
    String getHistoryVisibility();

}
