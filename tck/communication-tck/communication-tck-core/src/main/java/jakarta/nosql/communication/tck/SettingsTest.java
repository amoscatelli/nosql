/*
 *  Copyright (c) 2022 Contributors to the Eclipse Foundation
 *
 *  This program and the accompanying materials are made available under the
 *  terms of the Eclipse Public License v. 2.0 which is available at
 *  http://www.eclipse.org/legal/epl-2.0.
 *
 *  This Source Code may also be made available under the following Secondary
 *  Licenses when the conditions for such availability set forth in the Eclipse
 *  Public License v. 2.0 are satisfied: GNU General Public License v2.0
 *  w/Classpath exception which is available at
 *  https://www.gnu.org/software/classpath/license.html.
 *
 *  SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */
package jakarta.nosql.communication.tck;

import jakarta.nosql.Settings;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Supplier;

import static java.util.Collections.singletonMap;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SettingsTest {


    @BeforeEach
    public void beforeEach() {
        System.clearProperty("host");
        System.clearProperty("host.1");
        System.clearProperty("host.2");
        System.clearProperty("host.3");
        System.clearProperty("server");
        System.clearProperty("server.1");
        System.clearProperty("server.2");
    }
    @Test
    public void shouldReturnNPEWhenInstanceIsNull() {
        assertThrows(NullPointerException.class, () -> Settings.of((Map<String, Object>) null));

    }

    @Test
    public void shouldReturnNewInstance() {
        Settings settings = Settings.of();
        Assertions.assertNotNull(settings);
    }

    @Test
    public void shouldCreateFromMap() {
        Settings settings = Settings.of(singletonMap("key", "value"));
        assertFalse(settings.isEmpty());
    }

    @Test
    public void shouldContainsKeys() {
        Settings settings = Settings.of(singletonMap("key", "value"));
        assertTrue(settings.containsKey("key"));
        assertFalse(settings.containsKey("key2"));
    }


    @Test
    public void shouldGetKeys() {
        Settings settings = Settings.of(singletonMap("key", "value"));
        assertThat(settings.keySet()).contains("key");
    }


    @Test
    public void shouldSize() {
        Settings settings = Settings.of(singletonMap("key", "value"));
        assertTrue(settings.size() >= 1);

    }

    @Test
    public void shouldIsEmpty() {
        Settings settings = Settings.of(singletonMap("key", "value"));
        assertFalse(settings.isEmpty());
    }

    @Test
    public void shouldGet() {
        Settings settings = Settings.of(singletonMap("key", "12"));
        Optional<Object> value = settings.get("key");
        Assertions.assertNotNull(value);
        Assertions.assertEquals("12", value.get());
    }

    @Test
    public void shouldGetSupplier() {
        Settings settings = Settings.of(singletonMap("key", "12"));
        Optional<Object> value = settings.get(() -> "key");
        Assertions.assertNotNull(value);
        Assertions.assertEquals("12", value.get());
    }

    @Test
    public void shouldNPEGet() {
        Settings settings = Settings.of(singletonMap("key", "12"));
        Assertions.assertThrows(NullPointerException.class, () -> settings.get((String) null));
        Assertions.assertThrows(NullPointerException.class, () -> settings.get((Supplier<String>) null));
    }

    @Test
    public void shouldGetIterable() {
        Settings settings = Settings.of(singletonMap("key", "12"));
        Optional<Object> value = settings.get(Collections.singleton("key"));
        Assertions.assertNotNull(value);
        Assertions.assertEquals("12", value.get());
    }

    @Test
    public void shouldGetIterableSupplier() {
        Settings settings = Settings.of(singletonMap("key", "12"));
        Optional<Object> value = settings.getSupplier(Collections.singleton(() -> "key"));
        Assertions.assertNotNull(value);
        Assertions.assertEquals("12", value.get());
    }

    @Test
    public void shouldNPEGetIterable() {
        Settings settings = Settings.of(singletonMap("key", "12"));
        Assertions.assertThrows(NullPointerException.class, () -> settings.get((Iterable<String>) null));
        Assertions.assertThrows(NullPointerException.class, () -> settings.getSupplier(null));
    }

    @Test
    public void shouldGetValueClass() {
        Settings settings = Settings.of(singletonMap("key", "12"));

        Integer value = settings.get("key", Integer.class).get();
        assertEquals(Integer.valueOf(12), value);
        assertFalse(settings.get("key2", Integer.class).isPresent());
    }

    @Test
    public void shouldGetValueClassSupplier() {
        Settings settings = Settings.of(singletonMap("key", "12"));

        Integer value = settings.get(() -> "key", Integer.class).get();
        assertEquals(Integer.valueOf(12), value);
        assertFalse(settings.get(() -> "key2", Integer.class).isPresent());
    }

    @Test
    public void shouldGetOrDefault() {
        Settings settings = Settings.of(singletonMap("key", "12"));
        assertEquals("12", settings.getOrDefault("key", "13"));
        assertEquals("13", settings.getOrDefault("key-1", "13"));
    }

    @Test
    public void shouldGetOrDefaultSupplier() {
        Settings settings = Settings.of(singletonMap("key", "12"));
        assertEquals("12", settings.getOrDefault(() -> "key", "13"));
        assertEquals("13", settings.getOrDefault(() -> "key-1", "13"));
    }

    @Test
    public void shouldReturnErrorWhenPrefixIsNull() {
        Settings settings = Settings.builder()
                .put("host", "host")
                .put("host.1", "host-1")
                .put("host.2", "host-2")
                .put("host.3", "host-3")
                .build();
        assertThrows(NullPointerException.class, () -> settings.prefix((String) null));
    }

    @Test
    public void shouldFindPrefix() {
        Settings settings = Settings.builder()
                .put("host", "host")
                .put("host.1", "host-1")
                .put("host.2", "host-2")
                .put("host.3", "host-3")
                .build();

        List<Object> hosts = settings.prefix("host");
        assertThat(hosts)
                .hasSize(4)
                .contains("host", "host-1", "host-2", "host-3");
    }

    @Test
    public void shouldFindPrefixSupplier() {
        Settings settings = Settings.builder()
                .put("host", "host")
                .put("host.1", "host-1")
                .put("host.2", "host-2")
                .put("host.3", "host-3")
                .build();

        List<Object> hosts = settings.prefix(() -> "host");
        assertThat(hosts)
                .hasSize(4)
                .contains("host", "host-1", "host-2", "host-3");
    }

    @Test
    public void shouldFindPrefixWithOrder() {
        Settings settings = Settings.builder()
                .put("host", "host")
                .put("host.3", "host-3")
                .put("host.2", "host-2")
                .put("host.1", "host-1")
                .build();

        List<Object> hosts = settings.prefix("host");
        assertThat(hosts).hasSize(4).contains("host", "host-1", "host-2", "host-3");
    }


    @Test
    public void shouldReturnErrorWhenPrefixesIsNull() {
        Settings settings = Settings.builder()
                .put("host", "host")
                .put("host.1", "host-1")
                .put("host.2", "host-2")
                .put("host.3", "host-3")
                .build();
        assertThrows(NullPointerException.class, () -> settings.prefix((Collection<String>) null));

    }

    @Test
    public void shouldFindPrefixes() {
        Settings settings = Settings.builder()
                .put("host", "host")
                .put("host.1", "host-1")
                .put("server", "server")
                .put("server.1", "server-1")
                .build();

        List<Object> hosts = settings.prefix(Arrays.asList("host", "server"));
        assertThat(hosts).hasSize(4).contains("host", "host-1", "server", "server-1");
    }

    @Test
    public void shouldFindPrefixesSupplier() {
        Settings settings = Settings.builder()
                .put("host", "host")
                .put("host.1", "host-1")
                .put("server", "server")
                .put("server.1", "server-1")
                .build();

        List<Object> hosts = settings.prefixSupplier(Arrays.asList(() -> "host", () -> "server"));
        assertThat(hosts).hasSize(4).contains("host", "host-1", "server", "server-1");
    }

    @Test
    public void shouldFindPrefixesSort() {
        Settings settings = Settings.builder()
                .put("host.1", "host-1")
                .put("host", "host")
                .put("server.1", "server-1")
                .put("server", "server")
                .build();

        List<Object> hosts = settings.prefix(Arrays.asList("host", "server"));
        assertThat(hosts).hasSize(4).contains("host", "host-1", "server", "server-1");
    }

}
