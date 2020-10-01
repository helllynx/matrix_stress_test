/*
 * matrix-java-sdk - Matrix Client SDK for Java
 * Copyright (C) 2017 Kamax Sarl
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

package matrix.client;

import com.google.gson.JsonObject;

import matrix._MatrixContent;
import matrix._MatrixID;
import matrix._MatrixUser;
import matrix.hs._MatrixRoom;
import matrix.room.RoomAlias;
import matrix.room._RoomAliasLookup;
import matrix.room._RoomCreationOptions;

import java.io.File;
import java.net.URI;
import java.util.List;
import java8.util.Optional;

public interface _MatrixClient extends _MatrixClientRaw {

    _MatrixID getWhoAmI();

    void setDisplayName(String name);

    _RoomAliasLookup lookup(RoomAlias alias);

    _MatrixRoom createRoom(_RoomCreationOptions options);

    _MatrixRoom getRoom(String roomId);

    List<_MatrixRoom> getJoinedRooms();

    _MatrixRoom joinRoom(String roomIdOrAlias);

    _MatrixUser getUser(_MatrixID mxId);

    Optional<String> getDeviceId();

    /* Custom endpoint! */
    // TODO refactor into custom synapse class?
    void register(MatrixPasswordCredentials credentials, String sharedSecret, boolean admin);

    /***
     * Set the access token to use for any authenticated API calls.
     * 
     * @param accessToken
     *            The access token provided by the server which must be valid
     * @throws MatrixClientRequestException
     *             If an error occurred while checking for the identity behind the access token
     */
    void setAccessToken(String accessToken) throws MatrixClientRequestException;

    void login(MatrixPasswordCredentials credentials);

    void logout();

    _SyncData sync(_SyncOptions options);

    /**
     * Download content from the media repository
     * 
     * @param mxUri
     *            The MXC URI for the content to download
     * @return The content
     * @throws IllegalArgumentException
     *             if the parameter is not a valid MXC URI
     */
    _MatrixContent getMedia(String mxUri) throws IllegalArgumentException;

    /**
     * Download content from the media repository
     * 
     * @param mxUri
     *            The MXC URI for the content to download
     * @return The content
     * @throws IllegalArgumentException
     *             if the parameter is not a valid MXC URI
     */
    _MatrixContent getMedia(URI mxUri) throws IllegalArgumentException;

    /**
     * Upload content to the media repository
     * 
     * @param data
     *            The data to send
     * @param type
     *            The mime-type of the content upload
     * @return The MXC URI for the uploaded content
     */
    String putMedia(byte[] data, String type);

    /**
     * Upload content to the media repository
     * 
     * @param data
     *            The data to send
     * @param type
     *            The mime-type of the content upload
     * @param filename
     *            A suggested filename for the content
     * @return The MXC URI for the uploaded content
     */
    String putMedia(byte[] data, String type, String filename);

    /**
     * Upload content to the media repository
     *
     * @param data
     *            The file to read the data from
     * @param type
     *            The mime-type of the content upload
     * @return The MXC URI for the uploaded content
     */
    String putMedia(File data, String type);

    /**
     * Upload content to the media repository
     * 
     * @param data
     *            The data to send
     * @param type
     *            The mime-type of the content upload
     * @param filename
     *            A suggested filename for the content
     * @return The MXC URI for the uploaded content
     */
    String putMedia(File data, String type, String filename);

    List<JsonObject> getPushers();

    void setPusher(JsonObject pusher);

    void deletePusher(String pushKey);

    _GlobalPushRulesSet getPushRules();

    _PushRule getPushRule(String scope, String kind, String id);

}
