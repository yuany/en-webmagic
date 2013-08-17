package us.codecraft.webmagic.model.samples;

import org.apache.commons.lang3.builder.ToStringBuilder;

import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.model.PageModelPipeline;

public class WalmartPageModelPipeline  implements PageModelPipeline<Walmart> {

	@Override
	public void process(Walmart obj, Task task) {
		 System.out.println(ToStringBuilder.reflectionToString(obj));
	}


}
