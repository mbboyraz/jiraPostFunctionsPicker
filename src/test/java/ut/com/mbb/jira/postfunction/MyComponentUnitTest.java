package ut.com.mbb.jira.postfunction;

import org.junit.Test;
import com.mbb.jira.postfunction.api.MyPluginComponent;
import com.mbb.jira.postfunction.impl.MyPluginComponentImpl;

import static org.junit.Assert.assertEquals;

public class MyComponentUnitTest
{
    @Test
    public void testMyName()
    {
        MyPluginComponent component = new MyPluginComponentImpl(null);
        assertEquals("names do not match!", "myComponent",component.getName());
    }
}