package pl.pragmatists.concordion.rest;

import org.apache.commons.lang3.StringUtils;
import org.concordion.api.AbstractCommand;
import org.concordion.api.CommandCall;
import org.concordion.api.Element;
import org.concordion.api.Evaluator;
import org.concordion.api.Result;
import org.concordion.api.ResultRecorder;
import org.concordion.api.listener.AssertEqualsListener;
import org.concordion.api.listener.AssertFailureEvent;
import org.concordion.api.listener.AssertSuccessEvent;
import org.concordion.internal.listener.AssertResultRenderer;
import org.concordion.internal.util.Announcer;

public class ExpectedStatusCommand extends AbstractCommand {

    private Announcer<AssertEqualsListener> listeners = Announcer.to(AssertEqualsListener.class);
    
    public ExpectedStatusCommand() {
        listeners.addListener(new AssertResultRenderer());
    }
    
    @Override
    public void verify(CommandCall commandCall, Evaluator evaluator, ResultRecorder resultRecorder) {
        
        String expectedStatus = StringUtils.trim(commandCall.getElement().getText());
        RequestExecutor response = RequestExecutor.fromEvaluator(evaluator);
        String actualStatus = StringUtils.trim(response.getStatusLine());
        
        System.err.println("STATUS(Expected): " + expectedStatus);
        System.err.println("STATUS(Actual): " + actualStatus);
        
        if(expectedStatus.equals(actualStatus)){
            resultRecorder.record(Result.SUCCESS);
            announceSuccess(commandCall.getElement());
        } else {
            resultRecorder.record(Result.FAILURE);
            announceFailure(commandCall.getElement(), expectedStatus, actualStatus);
        }

    }

    private void announceSuccess(Element element) {
        listeners.announce().successReported(new AssertSuccessEvent(element));
    }

    private void announceFailure(Element element, String expected, Object actual) {
        listeners.announce().failureReported(new AssertFailureEvent(element, expected, actual));
    }
}
