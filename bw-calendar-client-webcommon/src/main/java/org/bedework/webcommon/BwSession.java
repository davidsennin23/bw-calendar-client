/* ********************************************************************
    Licensed to Jasig under one or more contributor license
    agreements. See the NOTICE file distributed with this work
    for additional information regarding copyright ownership.
    Jasig licenses this file to you under the Apache License,
    Version 2.0 (the "License"); you may not use this file
    except in compliance with the License. You may obtain a
    copy of the License at:

    http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing,
    software distributed under the License is distributed on
    an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
    KIND, either express or implied. See the License for the
    specific language governing permissions and limitations
    under the License.
*/

package org.bedework.webcommon;

import org.bedework.appcommon.TimeView;
import org.bedework.calfacade.BwCategory;
import org.bedework.calfacade.configs.AuthProperties;
import org.bedework.util.struts.Request;

import java.io.Serializable;
import java.util.Collection;

/** This interface represents a session for the Bedework web interface.
 * Some user state will be retained here.
 * We also provide a number of methods which act as the interface between
 * the web world and the calendar world.
 *
 * @author Mike Douglass   douglm   rpi.edu
 */
public interface BwSession extends Serializable {
  /** ===================================================================
   *                     Property methods
   *  =================================================================== */

  /**
   * Call to reset state and flush objects.
   * @param req
   */
  void reset(Request req);

  /** This may not be entirely correct so should be used with care.
   * Really just provides some measure of use.
   *
   * @return long session number
   */
  long getSessionNum();

  /** The current user
   *
   * @param val   String user
   */
  void setUser(String val);

  /**
   * @return String
   */
  String getUser();

  /** Is this a guest user?
   *
   * @return boolean true for a guest
   */
  boolean isGuest();

  /** Prepare state of session for render
   *
   * @param req
   */
  void prepareRender(BwRequest req);

  /**
   *
   * @param req
   * @throws Throwable
   */
  void embedFilters(final BwRequest req) throws Throwable;

  /** Get the current view according to the current setting of curViewPeriod.
   * May be called when we change the view or if we need a refresh
   *
   * @param req
   * @return current time view
   */
  TimeView getCurTimeView(BwRequest req);

  /**
   * @return AuthProperties object
   */
  AuthProperties getAuthpars();

  /** Make these available
   *
   * @param request
   * @throws Throwable
   */
  void embedUserCollections(BwRequest request) throws Throwable;

  /** Return a list of calendars in which calendar objects can be
   * placed by the current user.
   *
   * <p>Caldav currently does not allow collections inside collections so that
   * calendar collections are the leaf nodes only.
   *
   * @param request
   * @throws Throwable
   */
  void embedAddContentCalendarCollections(BwRequest request) throws Throwable;

  /* ====================================================================
   *                   Categories
   * ==================================================================== */

  /** Embed the list of categories for this owner. Return a null list for
   * exceptions or no categories. For guest mode or public admin this is the
   * same as getPublicCategories. This is the method to call unless you
   * specifically want a list of public categories (for search of public events
   * perhaps.)
   *
   * @param request
   * @param refresh
   * @param kind
   * @throws Throwable
   * @return collection of categories
   */
  Collection<BwCategory> embedCategories(BwRequest request,
                                         boolean refresh,
                                         int kind) throws Throwable;

  /* Kind of entity we are referring to */

  public static final int ownersEntity = 1;
  public static final int editableEntity = 2;
  public static final int preferredEntity = 3;
  public static final int defaultEntity = 4;

  /**
   *
   * @param request
   * @param kind
   * @throws Throwable
   */
  void embedContactCollection(BwRequest request,
                              final int kind) throws Throwable;

  /** Called by jsp when editing an event
   *
   * @param request
   * @param kind
   * @throws Throwable
   */
  void embedLocations(final BwRequest request,
                      final int kind) throws Throwable;
}

