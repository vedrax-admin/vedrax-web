package com.vedrax.util;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.util.FileCopyUtils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UncheckedIOException;

import static java.nio.charset.StandardCharsets.UTF_8;

public class ResourceUtils {

  /**
   * Get resource file as string
   *
   * @param name the resource file name
   * @return the string
   */
  public static String readResourceToString(String name) {
    Resource resource = new ClassPathResource(name);
    return asString(resource);
  }

  /**
   * Helper method for transforming a resource to string
   *
   * @param resource the resource
   * @return string representation
   */
  private static String asString(Resource resource) {
    try (Reader reader = new InputStreamReader(resource.getInputStream(), UTF_8)) {
      return FileCopyUtils.copyToString(reader);
    } catch (IOException ex) {
      throw new UncheckedIOException(ex);
    }
  }
}
