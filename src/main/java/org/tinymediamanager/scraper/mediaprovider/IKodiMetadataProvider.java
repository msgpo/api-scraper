/*
 * Copyright 2012 - 2016 Manuel Laggner
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
package org.tinymediamanager.scraper.mediaprovider;

import java.util.List;

import org.tinymediamanager.scraper.entities.MediaType;

/**
 * just a dedicated interface, for JSPF to find all "special" Kodi impls.<br>
 * 
 * @author Myron Boyle
 *
 */
public interface IKodiMetadataProvider extends IMediaProvider {
  /**
   * get all Kodi scraper-plugins for the desired type
   * 
   * @param type
   *          the desired media type
   * @return all found plugins
   */
  public List<IMediaProvider> getPluginsForType(MediaType type);
}
