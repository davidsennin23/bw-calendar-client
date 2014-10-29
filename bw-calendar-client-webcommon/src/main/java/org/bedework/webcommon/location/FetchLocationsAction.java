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

package org.bedework.webcommon.location;

import org.bedework.appcommon.client.Client;
import org.bedework.calfacade.BwLocation;
import org.bedework.webcommon.BwAbstractAction;
import org.bedework.webcommon.BwActionFormBase;
import org.bedework.webcommon.BwRequest;
import org.bedework.webcommon.BwSession;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;

/** This action fetches locations and writes them as a json object.
 *
 * <p>Forwards to:<ul>
 *      <li>null     always.</li>
 * </ul>
 *
 * <p>Request param is kind: value may be <ul>
 *      <li>"owners"       all owners locations.</li>
 *      <li>"editable"     the locations we can edit.</li>
 *      <li>"preferred"    our preferred locations.</li>
 * </ul>
 *
 * Default is "owners"</p>
 *
 * @author Mike Douglass   douglm   rpi.edu
 */
public class FetchLocationsAction extends BwAbstractAction {
  protected ObjectMapper mapper = new ObjectMapper(); // create once, reuse

  @Override
  public int doAction(final BwRequest request,
                      final BwActionFormBase form) throws Throwable {
    if (debug) {
      mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
    }

    final DateFormat df = new SimpleDateFormat("yyyy'-'MM'-'dd'T'HH':'mm':'ss'Z'");

    mapper.setDateFormat(df);

    mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

    final Client cl = request.getClient();
    form.setNocache(false);
    final String changeToken = cl.getCurrentChangeToken();

    final String ifNoneMatch = request.getRequest().getHeader("if-none-match");

    if ((changeToken != null) && changeToken.equals(ifNoneMatch)) {
      request.getResponse().setStatus(HttpServletResponse.SC_NOT_MODIFIED);
      return forwardNull;
    }

      /* Add an etag */
    request.getResponse().addHeader("etag", changeToken);

    final BwSession sess = request.getSess();

    final Collection<BwLocation> vals;
    final String attrName;

    final String kind = request.getReqPar("kind", "owners");

    switch (kind) {
      case "owners":
        attrName = BwRequest.bwLocationsListName;
        vals = sess.getLocations(request, BwSession.ownersEntity,
                                 true);
        break;
      case "editable":
        attrName = BwRequest.bwEditableLocationsListName;

        vals = sess.getLocations(request, BwSession.editableEntity,
                                 false);
//    } else if (kind.equals("preferred")) {
//      attrName = BwRequest.bwPreferredLocationsListName;
//
//      vals = curAuthUserPrefs.getLocationPrefs().getPreferred();
        break;
      default:
        request.getResponse().setStatus(
                HttpServletResponse.SC_BAD_REQUEST);
        return forwardNull;
    }

    request.getResponse().setContentType("text/json; charset=UTF-8");

    writeJson(request.getResponse(), vals);
    request.getResponse().getOutputStream().close();

    return forwardNull;
  }

  protected void writeJson(final HttpServletResponse resp,
                           final Object val) throws ServletException {
    try {
      mapper.writeValue(resp.getOutputStream(), val);
    } catch (final Throwable t) {
      throw new ServletException(t);
    }
  }
}