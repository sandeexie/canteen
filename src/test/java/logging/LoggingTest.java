package logging;

import com.github.editor.log.Logging;
import com.github.editor.log.LoggingFactory;
import org.junit.Test;

/**
 * 日志门面测试
 * @date 2020-10-31
 * @author xyf
 */
public class LoggingTest {


	@Test
	public void warning(){
		Logging logging = LoggingFactory.create();
		logging.getLogger();
		logging.logWarning("sandee");
	}
}
