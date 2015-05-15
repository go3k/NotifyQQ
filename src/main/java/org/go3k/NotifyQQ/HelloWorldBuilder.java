package org.go3k.NotifyQQ;
import hudson.Launcher;
import hudson.Extension;
import hudson.util.FormValidation;
import hudson.model.AbstractBuild;
import hudson.model.JobProperty;
import hudson.model.JobPropertyDescriptor;
import hudson.model.BuildListener;
import hudson.model.AbstractProject;
import hudson.tasks.Builder;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Publisher;
import hudson.tasks.Notifier;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.QueryParameter;
import jenkins.tasks.SimpleBuildStep;

import javax.servlet.ServletException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import java.io.InputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import javax.xml.bind.DatatypeConverter;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import hudson.tasks.BuildStepMonitor;

import org.jenkinsci.plugins.tokenmacro.TokenMacro;

/**
 * Sample {@link Builder}.
 *
 * <p>
 * When the user configures the project and enables this builder,
 * {@link DescriptorImpl#newInstance(StaplerRequest)} is invoked
 * and a new {@link HelloWorldBuilder} is created. The created
 * instance is persisted to the project configuration XML by using
 * XStream, so this allows you to use instance fields (like {@link #name})
 * to remember the configuration.
 *
 * <p>
 * When a build is performed, the {@link #perform(AbstractBuild, Launcher, BuildListener)}
 * method will be invoked. 
 *
 * @author Kohsuke Kawaguchi
 */
public class HelloWorldBuilder extends 
    // Builder {
    Notifier {
    // JobProperty<AbstractProject<?, ?>> {
    // private final String qqnumber;
    private final List<QQNumber> qQNumbers;
    private final String qqmessage;

    public static int test = 0;

    private PrintStream logger;

    // Fields in config.jelly must match the parameter names in the "DataBoundConstructor"
    @DataBoundConstructor
    public HelloWorldBuilder(List<QQNumber> qQNumbers, String qqmessage) {
        // this.qQNumbers = qQNumbers;
        this.qQNumbers = new ArrayList<QQNumber>( qQNumbers );
        this.qqmessage = qqmessage;
    }

    public List<QQNumber> getQQNumbers() {
        return qQNumbers;
    }

    public String getQqmessage() {
        return qqmessage;
    }

    public BuildStepMonitor getRequiredMonitorService() {
        return BuildStepMonitor.NONE;
    }

    @Override
    public boolean perform(AbstractBuild build, Launcher launcher, BuildListener listener) {
        // This is where you 'build' the project.
        // Since this is a dummy, we just say 'hello world' and call that a build.

        logger = listener.getLogger();

        // String temmessage = "temmessage:  ${JOB_NAME}  ${BUILD_NUMBER}  ${JOB_URL}";
        // try
        // {
        //     String text = TokenMacro.expandAll( build, listener, temmessage );
        //     logger.println(text);
        // }
        // catch (Exception e)
        // {
        //     logger.println("tokenmacro expand error.");
        // }

        
        for (int i = 0; i < qQNumbers.size(); i++) {
            QQNumber number = (QQNumber)qQNumbers.get(i);
            send(GenerateMessageURL(number.GetUrlString(), qqmessage));
        }
        logger.println("test code is: " + HelloWorldBuilder.test);

        return true;
    }

    private String GenerateMessageURL(String qq, String msg)
    {
        return String.format("http://127.0.0.1:5000/openqq/%s&content=%s", qq, msg);
    }

    protected void send(String url){
        logger.println("Sendurl: " + url);
        
        HttpURLConnection connection = null;
        InputStream is = null;
        String resultData = "";
        try {
            URL targetUrl = new URL(url);
            connection = (HttpURLConnection) targetUrl.openConnection();
            connection.connect();
            is = connection.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);  
            BufferedReader bufferReader = new BufferedReader(isr);
            String inputLine = "";  
            while((inputLine = bufferReader.readLine()) != null){  
                resultData += inputLine + "\n";  
            }

            logger.println("response: " + resultData);
        } catch (Exception e) {
            logger.println("http error." + e);
        } finally {
          if(is != null){  
                try {  
                    is.close();  
                } catch (IOException e) {  
                    // TODO Auto-generated catch block  
                    e.printStackTrace();  
                }  
            }  
            if(connection != null){  
                connection.disconnect();  
            }
        }
    }

    // Overridden for better type safety.
    // If your plugin doesn't really define any property on Descriptor,
    // you don't have to do this.
    @Override
    public DescriptorImpl getDescriptor() {
        return (DescriptorImpl)super.getDescriptor();
    }

    /**
     * Descriptor for {@link HelloWorldBuilder}. Used as a singleton.
     * The class is marked as public so that it can be accessed from views.
     *
     * <p>
     * See <tt>src/main/resources/hudson/plugins/hello_world/HelloWorldBuilder/*.jelly</tt>
     * for the actual HTML fragment for the configuration screen.
     */
    @Extension // This indicates to Jenkins that this is an implementation of an extension point.
    public static final class DescriptorImpl extends 
        BuildStepDescriptor<Publisher> {
        // JobPropertyDescriptor {
        /**
         * To persist global configuration information,
         * simply store it in a field and call save().
         *
         * <p>
         * If you don't want fields to be persisted, use <tt>transient</tt>.
         */

        /**
         * In order to load the persisted global configuration, you have to 
         * call load() in the constructor.
         */
        public DescriptorImpl() {
            load();
        }

        /**
         * Performs on-the-fly validation of the form field 'name'.
         *
         * @param value
         *      This parameter receives the value that the user has typed.
         * @return
         *      Indicates the outcome of the validation. This is sent to the browser.
         *      <p>
         *      Note that returning {@link FormValidation#error(String)} does not
         *      prevent the form from being saved. It just means that a message
         *      will be displayed to the user. 
         */
        // public FormValidation doCheckName(@QueryParameter String value)
        //         throws IOException, ServletException {
        //     if (value.length() == 0)
        //         return FormValidation.error("Please set a name");
        //     if (value.length() < 4)
        //         return FormValidation.warning("Isn't the name too short?");
        //     return FormValidation.ok();
        // }

        public FormValidation doCheckNumber(@QueryParameter String value)
                throws IOException, ServletException {
            if (value.length() <= 4)
                return FormValidation.error("你QQ号太短了吧。。。");
            else if (value.length() > 15)
                return FormValidation.error("QQ号有这么长吗？");
            else if (!isNumeric(value))
                return FormValidation.error("QQ号格式不对，数字数字数字！");
            return FormValidation.ok();
        }

        private boolean isNumeric(String str){
            Pattern pattern = Pattern.compile("[0-9]*");
            Matcher isNum = pattern.matcher(str);
            if( !isNum.matches() ){
               return false;
            }
            return true;
        }

        public FormValidation doCheckQqmessage(@QueryParameter String value)
                throws IOException, ServletException {
            return FormValidation.ok();
        }

        public boolean isApplicable(Class<? extends AbstractProject> aClass) {
            // Indicates that this builder can be used with all kinds of project types 
            return true;
        }

        /**
         * This human readable name is used in the configuration screen.
         */
        public String getDisplayName() {
            return "QQ通知";
        }

        @Override
        public boolean configure(StaplerRequest req, JSONObject formData) throws FormException {
            // To persist global configuration information,
            // set that to properties and call save().
            // ^Can also use req.bindJSON(this, formData);
            //  (easier when there are many fields; need set* methods for this, like setUseFrench)
            save();
            return super.configure(req,formData);
        }

        /**
         * This method returns true if the global configuration says we should speak French.
         *
         * The method name is bit awkward because global.jelly calls this method to determine
         * the initial state of the checkbox by the naming convention.
         */
    }
}

