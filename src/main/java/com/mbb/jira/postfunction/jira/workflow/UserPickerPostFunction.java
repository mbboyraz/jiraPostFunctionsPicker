package com.mbb.jira.postfunction.jira.workflow;

import com.atlassian.jira.issue.MutableIssue;
import com.atlassian.jira.workflow.function.issue.AbstractJiraFunctionProvider;
import com.atlassian.plugin.spring.scanner.annotation.component.Scanned;
import com.opensymphony.module.propertyset.PropertySet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * This is the post-function class that gets executed at the end of the transition.
 * Any parameters that were saved in your factory class will be available in the transientVars Map.
 */
@Scanned
public class UserPickerPostFunction extends AbstractJiraFunctionProvider
{
    private static final Logger log = LoggerFactory.getLogger(UserPickerPostFunction.class);
    public static final String FIELD_MESSAGE = "messageField";


    public void execute(Map transientVars, Map args, PropertySet ps) {
        MutableIssue issue = getIssue(transientVars);
        String message = (String)transientVars.get(FIELD_MESSAGE);


        if (null == message) {
            message = "";
        }

        issue.setDescription(issue.getDescription() + message);
    }
}