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

import matrix._MatrixContent;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java8.util.Optional;


import okhttp3.Request;

public class MatrixHttpContent extends AMatrixHttpClient implements _MatrixContent {

    private Logger log = LoggerFactory.getLogger(MatrixHttpContent.class);
    private final Pattern filenamePattern = Pattern.compile("filename=\"?([^\";]+)");
    private URI address;

    private MatrixHttpContentResult result;

    private boolean loaded = false;
    private boolean valid = false;

    public MatrixHttpContent(MatrixClientContext context, URI address) {
        super(context);
        this.address = address;
    }

    // TODO switch a HTTP HEAD to fetch initial data, instead of loading in memory directly
    private synchronized void load() {
        if (loaded) {
            return;
        }

        try {
            if (!StringUtils.equalsIgnoreCase("mxc", address.getScheme())) {
                log.debug("{} is not a supported protocol for avatars, ignoring", address.getScheme());
            } else {
                MatrixHttpRequest request = new MatrixHttpRequest(new Request.Builder().url(getPermaLink()));
                result = executeContentRequest(request);
                valid = result.isValid();
            }

        } catch (MatrixClientRequestException e) {
            valid = false;
        }
        loaded = true;
    }

    @Override
    public URI getAddress() {
        return address;
    }

    @Override
    public URL getPermaLink() {
        return getMediaPathBuilder("download", address.getHost()).addEncodedPathSegments(address.getPath().substring(1))
                .build().url();
    }

    @Override
    public boolean isValid() {
        load();

        return valid;
    }

    @Override
    public Optional<String> getType() {
        load();

        if (!isValid()) {
            throw new IllegalStateException("This method should only be called, if valid is true.");
        }
        return result.getContentType();
    }

    @Override
    public byte[] getData() {
        load();

        if (!isValid()) {
            throw new IllegalStateException("This method should only be called, if valid is true.");
        }
        return result.getData();
    }

    @Override
    public Optional<String> getFilename() {
        load();

        if (!isValid()) {
            throw new IllegalStateException("This method should only be called, if valid is true.");
        }

        return result.getHeader("Content-Disposition").filter(l -> !l.isEmpty()).flatMap(l -> {
            for (String v : l) {
                Matcher m = filenamePattern.matcher(v);
                if (m.find()) {
                    return Optional.of(m.group(1));
                }
            }

            return Optional.empty();
        });
    }

}
