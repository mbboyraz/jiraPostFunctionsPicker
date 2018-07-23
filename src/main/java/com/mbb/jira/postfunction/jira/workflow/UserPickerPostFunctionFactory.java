package com.mbb.jira.postfunction.jira.workflow;

import com.atlassian.jira.config.ConstantsManager;
import com.atlassian.jira.config.IssueTypeManager;
import com.atlassian.jira.issue.CustomFieldManager;
import com.atlassian.jira.issue.customfields.searchers.UserPickerSearcher;
import com.atlassian.jira.issue.fields.CustomField;
import com.atlassian.jira.issue.issuetype.IssueType;
import com.atlassian.jira.plugin.workflow.AbstractWorkflowPluginFactory;
import com.atlassian.jira.plugin.workflow.WorkflowPluginFunctionFactory;
import com.atlassian.jira.util.collect.MapBuilder;
import com.atlassian.plugin.spring.scanner.annotation.component.Scanned;
import com.atlassian.plugin.spring.scanner.annotation.imports.JiraImport;
import com.opensymphony.workflow.loader.AbstractDescriptor;
import com.opensymphony.workflow.loader.ConditionDescriptor;
import webwork.action.ActionContext;

import javax.inject.Inject;
import java.util.*;

/**
 * This is the factory class responsible for dealing with the UI for the post-function.
 * This is typically where you put default values into the velocity context and where you store user input.
 */
@Scanned
public class UserPickerPostFunctionFactory extends AbstractWorkflowPluginFactory implements WorkflowPluginFunctionFactory
{
    public static final String FIELD_MESSAGE = "messageField";

    @JiraImport
    private final ConstantsManager constantsManager;

    @JiraImport
    private final IssueTypeManager issueTypeManager;
    @JiraImport
    private final CustomFieldManager customFieldManager;
    //private WorkflowManager workflowManager;
    Collection<UserPickerSearcher> userPickerSearchers;


    @Inject
    public UserPickerPostFunctionFactory(CustomFieldManager customFieldManager, IssueTypeManager issueTypeManager, ConstantsManager constantsManager) {


        this.issueTypeManager = issueTypeManager;
        this.customFieldManager = customFieldManager;
        this.constantsManager = constantsManager;

    }

    @Override
    protected void getVelocityParamsForInput(Map<String, Object> velocityParams) {
        Map<String, String[]> myParams = ActionContext.getParameters();

        //final JiraWorkflow jiraWorkflow = workflowManager.getWorkflow(myParams.get("workflowName")[0]);
        Collection<IssueType> issueTypes = issueTypeManager.getIssueTypes();
        Collection<CustomField> customFields = customFieldManager.getCustomFieldObjects();
        for(CustomField customField : customFields){
            if(customField.getCustomFieldType().equals("User Picker")){
                userPickerSearchers.add((UserPickerSearcher) customField);
            }
        }
        //the default message
        velocityParams.put("custom Fields", Collections.unmodifiableCollection(customFields));


    }


    @Override
    protected void getVelocityParamsForEdit(Map<String, Object> velocityParams, AbstractDescriptor descriptor) {
        getVelocityParamsForInput(velocityParams);
        velocityParams.put("selectedCustomFields", getSelectedCustomIds(descriptor));
    }


    @Override
    protected void getVelocityParamsForView(Map<String, Object> velocityParams, AbstractDescriptor descriptor) {

        Collection selectedCustomFieldIds = getSelectedCustomIds(descriptor);
        List<CustomField> selectedCustomField = new ArrayList<>();
        for (Object selectedCustomId : selectedCustomFieldIds) {
            String customFieldId = (String) selectedCustomId;
            CustomField customField = customFieldManager.getCustomFieldObject(customFieldId);
            if (selectedCustomField != null) {
                selectedCustomField.add(customField);
            }
        }


        velocityParams.put("customs", Collections.unmodifiableCollection(selectedCustomField));
    }


    public Map<String, ?> getDescriptorParams(Map formParams) {
        Map params = new HashMap();
        Collection customids = formParams.keySet();
        StringBuilder custIds = new StringBuilder();
        for (Object custumId : customids) {
            custIds.append((String) custumId).append(",");

        }


        return MapBuilder.build("customs", custIds.substring(0, custIds.length() - 1));
    }

    private Collection getSelectedCustomIds(AbstractDescriptor descriptor) {
        Collection<String> selectedCustomIds = new ArrayList<>();
        if (!(descriptor instanceof ConditionDescriptor)) {
            throw new IllegalArgumentException("Descriptor must be a ConditionDescriptor.");
        }

        ConditionDescriptor conditionDescriptor = (ConditionDescriptor) descriptor;

        String customF = (String) conditionDescriptor.getArgs().get("customs");
        StringTokenizer st = new StringTokenizer(customF, ",");

        while (st.hasMoreTokens()) {
            selectedCustomIds.add(st.nextToken());
        }
        return selectedCustomIds;
    }

}