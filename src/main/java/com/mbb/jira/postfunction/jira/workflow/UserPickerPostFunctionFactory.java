package com.mbb.jira.postfunction.jira.workflow;

import com.atlassian.jira.config.IssueTypeManager;
import com.atlassian.jira.issue.CustomFieldManager;

import com.atlassian.jira.issue.customfields.searchers.UserPickerSearcher;
import com.atlassian.jira.issue.fields.CustomField;
import com.atlassian.jira.issue.issuetype.IssueType;
import com.atlassian.jira.plugin.workflow.AbstractWorkflowPluginFactory;
import com.atlassian.jira.plugin.workflow.WorkflowPluginFunctionFactory;
import com.atlassian.jira.workflow.JiraWorkflow;
import com.atlassian.jira.workflow.WorkflowManager;
import com.atlassian.plugin.spring.scanner.annotation.imports.JiraImport;
import com.opensymphony.workflow.loader.AbstractDescriptor;
import com.opensymphony.workflow.loader.FunctionDescriptor;
import webwork.action.ActionContext;


import java.util.Collection;
import java.util.HashMap;

import java.util.Map;

/**
 * This is the factory class responsible for dealing with the UI for the post-function.
 * This is typically where you put default values into the velocity context and where you store user input.
 */

public class UserPickerPostFunctionFactory extends AbstractWorkflowPluginFactory implements WorkflowPluginFunctionFactory
{
    public static final String FIELD_MESSAGE = "messageField";

    private WorkflowManager workflowManager;
    Collection<UserPickerSearcher> userPickerSearchers;

    @JiraImport
    private final IssueTypeManager issueTypeManager;
    @JiraImport
    private final CustomFieldManager customFieldManager;



    public UserPickerPostFunctionFactory(WorkflowManager workflowManager, CustomFieldManager customFieldManager, IssueTypeManager issueTypeManager) {
        this.workflowManager = workflowManager;

        this.issueTypeManager = issueTypeManager;
        this.customFieldManager = customFieldManager;
    }

    @Override
    protected void getVelocityParamsForInput(Map<String, Object> velocityParams) {
        Map<String, String[]> myParams = ActionContext.getParameters();
        final JiraWorkflow jiraWorkflow = workflowManager.getWorkflow(myParams.get("workflowName")[0]);
        Collection<IssueType> issueTypes = issueTypeManager.getIssueTypes();
        Collection<CustomField> customFields = customFieldManager.getCustomFieldObjects();
        for(CustomField customField : customFields){
            if(customField.getCustomFieldType().equals("User Picker")){
                userPickerSearchers.add((UserPickerSearcher) customField);
            }
        }
        //the default message
        velocityParams.put(FIELD_MESSAGE, "Workflow Last Edited By " + jiraWorkflow.getUpdateAuthorName());


    }


    @Override
    protected void getVelocityParamsForEdit(Map<String, Object> velocityParams, AbstractDescriptor descriptor) {
        getVelocityParamsForInput(velocityParams);
        getVelocityParamsForView(velocityParams, descriptor);
    }

    @Override
    protected void getVelocityParamsForView(Map<String, Object> velocityParams, AbstractDescriptor descriptor) {
        if (!(descriptor instanceof FunctionDescriptor)) {
            throw new IllegalArgumentException("Descriptor must be a FunctionDescriptor.");
        }

        FunctionDescriptor functionDescriptor = (FunctionDescriptor)descriptor;
        String message = (String)functionDescriptor.getArgs().get(FIELD_MESSAGE);

        if (message == null) {
            message = "No Message";
        }

        velocityParams.put(FIELD_MESSAGE,message);
    }


    public Map<String,?> getDescriptorParams(Map<String, Object> formParams) {
        Map params = new HashMap();

        // Process The map
        String message = extractSingleParam(formParams,FIELD_MESSAGE);
        params.put(FIELD_MESSAGE,message);

        return params;
    }

}