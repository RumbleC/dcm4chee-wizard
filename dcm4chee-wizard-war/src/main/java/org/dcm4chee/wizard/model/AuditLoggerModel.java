/* ***** BEGIN LICENSE BLOCK *****
 * Version: MPL 1.1/GPL 2.0/LGPL 2.1
 *
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * The Original Code is part of dcm4che, an implementation of DICOM(TM) in
 * Java(TM), hosted at https://github.com/dcm4che.
 *
 * The Initial Developer of the Original Code is
 * Agfa Healthcare.
 * Portions created by the Initial Developer are Copyright (C) 2012
 * the Initial Developer. All Rights Reserved.
 *
 * Contributor(s):
 * See @authors listed below
 *
 * Alternatively, the contents of this file may be used under the terms of
 * either the GNU General Public License Version 2 or later (the "GPL"), or
 * the GNU Lesser General Public License Version 2.1 or later (the "LGPL"),
 * in which case the provisions of the GPL or the LGPL are applicable instead
 * of those above. If you wish to allow use of your version of this file only
 * under the terms of either the GPL or the LGPL, and not to allow others to
 * use your version of this file under the terms of the MPL, indicate your
 * decision by deleting the provisions above and replace them with the notice
 * and other provisions required by the GPL or the LGPL. If you do not delete
 * the provisions above, a recipient may use your version of this file under
 * the terms of any one of the MPL, the GPL or the LGPL.
 *
 * ***** END LICENSE BLOCK ***** */

package org.dcm4chee.wizard.model;

import java.io.Serializable;

import org.dcm4che3.conf.api.ConfigurationException;
import org.dcm4che3.net.audit.AuditLogger;

/**
 * @author Robert David <robert.david@agfa.com>
 */
public class AuditLoggerModel extends ConfigNodeModel implements Serializable {

    private static final long serialVersionUID = 1L;

    public static String cssClass = "audit_logger";
    public static String toolTip = "Audit Logger";

    private AuditLogger auditLogger;
    private String text;

    public AuditLoggerModel(AuditLogger auditLogger) throws ConfigurationException {
        this.auditLogger = auditLogger;
        text = auditLogger.getApplicationName();
        if (auditLogger.getAuditRecordRepositoryDevice() == null)
            text = text + " - Warning: the Audit Record Repository of this Audit Logger is no longer set";
    }

    public AuditLogger getAuditLogger() {
        return auditLogger;
    }

    public void setAuditLogger(AuditLogger auditLogger) {
        this.auditLogger = auditLogger;
    }

    public String getText() {
        return text;
    }

    @Override
    public String getDescription() {
        return toolTip;
    }
}
