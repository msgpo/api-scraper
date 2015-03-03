/*
 * Copyright 2012 - 2015 Manuel Laggner
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
package org.tinymediamanager.scraper;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.tinymediamanager.scraper.MediaArtwork.FanartSizes;
import org.tinymediamanager.scraper.MediaArtwork.MediaArtworkType;
import org.tinymediamanager.scraper.MediaArtwork.PosterSizes;

import java.lang.reflect.Field;
import java.util.HashMap;

/**
 * This class is used to set the scrape options for scraping.
 * 
 * @author Manuel Laggner
 * @since 1.0
 * 
 */
public class MediaScrapeOptions {
  private MediaSearchResult       result;
  private MediaMetadata           metadata;
  private HashMap<String, String> ids                       = new HashMap<String, String>();
  private MediaType               type;
  private MediaArtworkType        artworkType               = MediaArtworkType.ALL;
  private MediaLanguages          language                  = MediaLanguages.en;
  private CountryCode             country                   = CountryCode.US;
  private boolean                 scrapeCollectionInfo      = false;
  private boolean                 scrapeImdbForeignLanguage = false;
  private FanartSizes             fanartSize                = FanartSizes.MEDIUM;           // default; will be overwritten by tmm settings
  private PosterSizes             posterSize                = PosterSizes.MEDIUM;           // default; will be overwritten by tmm settings

  public MediaScrapeOptions(MediaType type) {
    this.type = type;
  }

  public MediaSearchResult getResult() {
    return result;
  }

  public void setResult(MediaSearchResult result) {
    this.result = result;
  }

  public String getId(String key) {
    return ids.get(key);
  }

  public void setId(String key, String id) {
    this.ids.put(key, id);
  }

  public String getImdbId() {
    Object obj = ids.get(MediaMetadata.IMDBID);
    if (obj == null) {
      return "";
    }
    return obj.toString();
  }

  public int getTmdbId() {
    int id = 0;
    try {
      id = Integer.parseInt(ids.get(MediaMetadata.TMDBID));
    }
    catch (Exception e) {
      return 0;
    }
    return id;
  }

  public void setImdbId(String imdbId) {
    ids.put(MediaMetadata.IMDBID, imdbId);
  }

  public void setTmdbId(int tmdbId) {
    ids.put(MediaMetadata.TMDBID, String.valueOf(tmdbId));
  }

  public MediaArtworkType getArtworkType() {
    return artworkType;
  }

  public void setArtworkType(MediaArtworkType artworkType) {
    this.artworkType = artworkType;
  }

  public MediaMetadata getMetadata() {
    return metadata;
  }

  public void setMetadata(MediaMetadata metadata) {
    this.metadata = metadata;
  }

  public MediaType getType() {
    return type;
  }

  public MediaLanguages getLanguage() {
    return language;
  }

  public void setLanguage(MediaLanguages language) {
    this.language = language;
  }

  public CountryCode getCountry() {
    return country;
  }

  public void setCountry(CountryCode country) {
    this.country = country;
  }

  public boolean isScrapeCollectionInfo() {
    return scrapeCollectionInfo;
  }

  public void setScrapeCollectionInfo(boolean scrapeCollectionInfo) {
    this.scrapeCollectionInfo = scrapeCollectionInfo;
  }

  public boolean isScrapeImdbForeignLanguage() {
    return scrapeImdbForeignLanguage;
  }

  public void setScrapeImdbForeignLanguage(boolean scrapeImdbForeignLanguage) {
    this.scrapeImdbForeignLanguage = scrapeImdbForeignLanguage;
  }

  public FanartSizes getFanartSize() {
    return fanartSize;
  }

  public PosterSizes getPosterSize() {
    return posterSize;
  }

  public void setFanartSize(FanartSizes fanartSize) {
    this.fanartSize = fanartSize;
  }

  public void setPosterSize(PosterSizes posterSize) {
    this.posterSize = posterSize;
  }

  /**
   * <p>
   * Uses <code>ReflectionToStringBuilder</code> to generate a <code>toString</code> for the specified object.
   * </p>
   * 
   * @return the String result
   * @see ReflectionToStringBuilder#toString(Object)
   */
  @Override
  public String toString() {
    return (new ReflectionToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE) {
      @Override
      protected boolean accept(Field f) {
        return super.accept(f) && !f.getName().equals("metadata");
      }
    }).toString();
  }
}