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

package org.dcm4chee.wizard.edit.proxy;

import java.util.ArrayList;
import java.util.Arrays;

import org.apache.wicket.MarkupContainer;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.ajax.markup.html.form.AjaxFallbackButton;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxButton;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.dcm4che3.conf.api.ConfigurationException;
import org.dcm4che3.net.ApplicationEntity;
import org.dcm4che3.net.Dimse;
import org.dcm4chee.proxy.conf.ForwardRule;
import org.dcm4chee.proxy.conf.ProxyAEExtension;
import org.dcm4chee.proxy.conf.Schedule;
import org.dcm4chee.wizard.common.component.ExtendedForm;
import org.dcm4chee.wizard.common.component.ModalWindowRuntimeException;
import org.dcm4chee.wizard.common.component.secure.SecureSessionCheckPage;
import org.dcm4chee.wizard.model.ApplicationEntityModel;
import org.dcm4chee.wizard.model.DimseCollectionModel;
import org.dcm4chee.wizard.model.StringArrayModel;
import org.dcm4chee.wizard.model.proxy.ForwardRuleModel;
import org.dcm4chee.wizard.tree.ConfigTreeNode;
import org.dcm4chee.wizard.tree.ConfigTreeProvider;
import org.dcm4chee.wizard.validator.DestinationURIValidator;
import org.dcm4chee.wizard.validator.ForwardRuleValidator;
import org.dcm4chee.wizard.validator.SOPClassValidator;
import org.dcm4chee.wizard.validator.ScheduleValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Robert David <robert.david@agfa.com>
 * @author Michael Backhaus <michael.backhaus@agfa.com>
 */
public class CreateOrEditForwardRulePage extends SecureSessionCheckPage {

    private static final long serialVersionUID = 1L;

    private static Logger log = LoggerFactory.getLogger(CreateOrEditForwardRulePage.class);

    // mandatory
    private Model<String> commonNameModel;
    private StringArrayModel destinationURIModel;

    // optional
    private StringArrayModel callingAETitleModel;
    private DimseCollectionModel dimsesModel;
    private Model<Boolean> exclusiveUseDefinedTCModel;
    private Model<String> scheduleDaysModel;
    private Model<String> scheduleHoursModel;
    private Model<String> useCallingAETitleModel;
    private StringArrayModel sopClassModel;
    private Model<Boolean> runPIXQueryModel;
    private Model<String> mpps2DoseSrTemplateURIModel;
    private Model<String> doseSrIODTemplateURIModel;
    private Model<String> descriptionModel;

    public CreateOrEditForwardRulePage(final ModalWindow window, final ForwardRuleModel forwardRuleModel,
            final ConfigTreeNode aeNode) {
        super();

        ApplicationEntity applicationEntity = null;
        try {
            applicationEntity = ((ApplicationEntityModel) aeNode.getModel()).getApplicationEntity();
        } catch (ConfigurationException ce) {
            log.error(this.getClass().toString() + ": " + "Error modifying retry: " + ce.getMessage());
            log.debug("Exception", ce);
            throw new ModalWindowRuntimeException(ce.getLocalizedMessage());
        }

        add(new WebMarkupContainer("create-forwardRule-title").setVisible(forwardRuleModel == null));
        add(new WebMarkupContainer("edit-forwardRule-title").setVisible(forwardRuleModel != null));

        setOutputMarkupId(true);
        final ExtendedForm form = new ExtendedForm("form");
        form.setResourceIdPrefix("dicom.edit.forwardRule.");
        add(form);

        if (forwardRuleModel == null) {
            commonNameModel = Model.of();
            destinationURIModel = new StringArrayModel(null);
            callingAETitleModel = new StringArrayModel(null);
            dimsesModel = new DimseCollectionModel(null, 5);
            exclusiveUseDefinedTCModel = Model.of(false);
            scheduleDaysModel = Model.of();
            scheduleHoursModel = Model.of();
            useCallingAETitleModel = Model.of();
            sopClassModel = new StringArrayModel(null);
            runPIXQueryModel = Model.of(false);
            mpps2DoseSrTemplateURIModel = Model.of();
            doseSrIODTemplateURIModel = Model.of();
            descriptionModel = Model.of();
        } else {
            ForwardRule forwardRule = forwardRuleModel.getForwardRule();
            commonNameModel = Model.of(forwardRule.getCommonName());
            destinationURIModel = new StringArrayModel(forwardRule.getDestinationURI().toArray(new String[forwardRule.getDestinationURI().size()]));
            callingAETitleModel = new StringArrayModel(forwardRule.getCallingAETs().toArray(new String[forwardRule.getCallingAETs().size()]));
            dimsesModel = new DimseCollectionModel(forwardRuleModel.getForwardRule(), 5);
            exclusiveUseDefinedTCModel = Model.of(forwardRule.isExclusiveUseDefinedTC());
            scheduleDaysModel = Model.of(forwardRule.getReceiveSchedule().getDays());
            scheduleHoursModel = Model.of(forwardRule.getReceiveSchedule().getHours());
            useCallingAETitleModel = Model.of(forwardRule.getUseCallingAET());
            sopClassModel = new StringArrayModel(forwardRule.getSopClasses().toArray(new String[forwardRule.getSopClasses().size()]));
            runPIXQueryModel = Model.of(forwardRule.isRunPIXQuery());
            mpps2DoseSrTemplateURIModel = Model.of(forwardRule.getMpps2DoseSrTemplateURI());
            doseSrIODTemplateURIModel = Model.of(forwardRule.getDoseSrIODTemplateURI());
            descriptionModel = Model.of(forwardRule.getDescription());
        }

        form.add(new Label("commonName.label", new ResourceModel("dicom.edit.forwardRule.commonName.label"))).add(
                new TextField<String>("commonName", commonNameModel).setRequired(true).add(
                        new ForwardRuleValidator(commonNameModel.getObject(), applicationEntity)));

        form.add(new Label("destinationURI.label", new ResourceModel("dicom.edit.forwardRule.destinationURI.label")))
                .add(new TextArea<String>("destinationURI", destinationURIModel).setRequired(true).add(
                        new DestinationURIValidator()));

        final WebMarkupContainer optionalContainer = new WebMarkupContainer("optional");
        form.add(optionalContainer.setOutputMarkupId(true).setOutputMarkupPlaceholderTag(true).setVisible(false));

        optionalContainer.add(
                new Label("callingAETitle.label", new ResourceModel(
                        "dicom.edit.forwardRule.optional.callingAETitle.label"))).add(
                new TextArea<String>("callingAETitle", callingAETitleModel));

        optionalContainer
                .add(new Label("dimse.label", new ResourceModel("dicom.edit.forwardRule.optional.dimse.label")));
        ArrayList<Dimse> dimseList = new ArrayList<Dimse>();
        dimseList.addAll(Arrays.asList(Dimse.values()));

        // remove DIMSEs not supported by proxy
        dimseList.remove(Dimse.N_DELETE_RQ);
        dimseList.remove(Dimse.N_DELETE_RSP);
        dimseList.remove(Dimse.N_GET_RQ);
        dimseList.remove(Dimse.N_GET_RSP);
        dimseList.remove(Dimse.C_CANCEL_RQ);

        DropDownChoice<Dimse> dimseDropDown1 = new DropDownChoice<Dimse>("dimse1", dimsesModel.getDimseModel(0),
                dimseList);
        optionalContainer.add(dimseDropDown1.setNullValid(true));
        DropDownChoice<Dimse> dimseDropDown2 = new DropDownChoice<Dimse>("dimse2", dimsesModel.getDimseModel(1),
                dimseList);
        optionalContainer.add(dimseDropDown2.setNullValid(true));
        DropDownChoice<Dimse> dimseDropDown3 = new DropDownChoice<Dimse>("dimse3", dimsesModel.getDimseModel(2),
                dimseList);
        optionalContainer.add(dimseDropDown3.setNullValid(true));
        DropDownChoice<Dimse> dimseDropDown4 = new DropDownChoice<Dimse>("dimse4", dimsesModel.getDimseModel(3),
                dimseList);
        optionalContainer.add(dimseDropDown4.setNullValid(true));
        DropDownChoice<Dimse> dimseDropDown5 = new DropDownChoice<Dimse>("dimse5", dimsesModel.getDimseModel(4),
                dimseList);
        optionalContainer.add(dimseDropDown5.setNullValid(true));

        optionalContainer.add(
                new Label("exclusiveUseDefinedTC.label", new ResourceModel(
                        "dicom.edit.forwardRule.optional.exclusiveUseDefinedTC.label"))).add(
                new CheckBox("exclusiveUseDefinedTC", exclusiveUseDefinedTCModel));

        optionalContainer
                .add(new Label("scheduleDays.label", new ResourceModel(
                        "dicom.edit.forwardRule.optional.scheduleDays.label"))).add(
                        new TextField<String>("scheduleDays", scheduleDaysModel).add(new ScheduleValidator(
                                ScheduleValidator.Type.DAYS)));

        optionalContainer.add(
                new Label("scheduleHours.label", new ResourceModel(
                        "dicom.edit.forwardRule.optional.scheduleHours.label"))).add(
                new TextField<String>("scheduleHours", scheduleHoursModel).add(new ScheduleValidator(
                        ScheduleValidator.Type.HOURS)));

        optionalContainer.add(
                new Label("useCallingAETitle.label", new ResourceModel(
                        "dicom.edit.forwardRule.optional.useCallingAETitle.label"))).add(
                new TextField<String>("useCallingAETitle", useCallingAETitleModel));

        optionalContainer.add(
                new Label("sopClass.label", new ResourceModel("dicom.edit.forwardRule.optional.sopClass.label"))).add(
                new TextArea<String>("sopClass", sopClassModel).add(new SOPClassValidator()));

        optionalContainer.add(
                new Label("runPIXQuery.label", new ResourceModel("dicom.edit.forwardRule.optional.runPIXQuery.label")))
                .add(new CheckBox("runPIXQuery", runPIXQueryModel));

        optionalContainer.add(
                new Label("mpps2DoseSrTemplateURI.label", new ResourceModel(
                        "dicom.edit.forwardRule.optional.mpps2DoseSrTemplateURI.label"))).add(
                new TextField<String>("mpps2DoseSrTemplateURI", mpps2DoseSrTemplateURIModel));

        optionalContainer.add(
                new Label("doseSrIODTemplateURI.label", new ResourceModel(
                        "dicom.edit.forwardRule.optional.doseSrIODTemplateURI.label"))).add(
                new TextField<String>("doseSrIODTemplateURI", doseSrIODTemplateURIModel));

        optionalContainer.add(
                new Label("description.label", new ResourceModel("dicom.edit.forwardRule.optional.description.label")))
                .add(new TextField<String>("description", descriptionModel));

        form.add(new Label("toggleOptional.label", new ResourceModel("dicom.edit.toggleOptional.label"))).add(
                new AjaxCheckBox("toggleOptional", new Model<Boolean>()) {
                    private static final long serialVersionUID = 1L;

                    @Override
                    protected void onUpdate(AjaxRequestTarget target) {
                        target.add(optionalContainer.setVisible(this.getModelObject()));
                    }
                });

        addSubmitButton(window, forwardRuleModel, aeNode, form);
        addCancelButton(window, form);
    }

    private MarkupContainer addCancelButton(final ModalWindow window, final ExtendedForm form) {
        return form.add(new AjaxFallbackButton("cancel", new ResourceModel("cancelBtn"), form) {

            private static final long serialVersionUID = 1L;

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                window.close(target);
            }

            @Override
            protected void onError(AjaxRequestTarget arg0, Form<?> arg1) {
            }
        }.setDefaultFormProcessing(false));
    }

    private void addSubmitButton(final ModalWindow window, final ForwardRuleModel forwardRuleModel,
            final ConfigTreeNode aeNode, final ExtendedForm form) {
        form.add(new IndicatingAjaxButton("submit", new ResourceModel("saveBtn"), form) {

            private static final long serialVersionUID = 1L;

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                try {
                    ForwardRule forwardRule = forwardRuleModel == null ? new ForwardRule() : forwardRuleModel
                            .getForwardRule();

                    forwardRule.setCommonName(commonNameModel.getObject());
                    forwardRule.setDestinationURIs(Arrays.asList(destinationURIModel.getArray()));
                    forwardRule.setCallingAETs(Arrays.asList(callingAETitleModel.getArray()));
                    forwardRule.setDimse(new ArrayList<Dimse>(dimsesModel.getDimses()));
                    forwardRule.setExclusiveUseDefinedTC(exclusiveUseDefinedTCModel.getObject());
                    Schedule schedule = new Schedule();
                    schedule.setDays(scheduleDaysModel.getObject());
                    schedule.setHours(scheduleHoursModel.getObject());
                    forwardRule.setReceiveSchedule(schedule);
                    forwardRule.setUseCallingAET(useCallingAETitleModel.getObject());
                    forwardRule.setSopClasses(Arrays.asList(sopClassModel.getArray()));
                    forwardRule.setRunPIXQuery(runPIXQueryModel.getObject());
                    forwardRule.setMpps2DoseSrTemplateURI(mpps2DoseSrTemplateURIModel.getObject());
                    forwardRule.setDoseSrIODTemplateURI(doseSrIODTemplateURIModel.getObject());
                    forwardRule.setDescription(descriptionModel.getObject());

                    ProxyAEExtension proxyAEExtension = ((ApplicationEntityModel) aeNode.getModel())
                            .getApplicationEntity().getAEExtension(ProxyAEExtension.class);

                    if (forwardRuleModel == null)
                        proxyAEExtension.getForwardRules().add(forwardRule);

                    ConfigTreeProvider.get().mergeDevice(
                            ((ApplicationEntityModel) aeNode.getModel()).getApplicationEntity().getDevice());
                    window.close(target);
                } catch (Exception e) {
                    log.error(this.getClass().toString() + ": " + "Error modifying forward rule: " + e.getMessage());
                    log.debug("Exception", e);
                    throw new ModalWindowRuntimeException(e.getLocalizedMessage());
                }
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
                if (target != null)
                    target.add(form);
            }
        });
    }
}
