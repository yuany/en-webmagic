package us.codecraft.webmagic.model.samples;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.model.AfterExtractor;
import us.codecraft.webmagic.model.OOSpider;
import us.codecraft.webmagic.model.annotation.ComboExtract;
import us.codecraft.webmagic.model.annotation.ComboExtract.OP;
import us.codecraft.webmagic.model.annotation.ConfigInfo;
import us.codecraft.webmagic.model.annotation.ExprType;
import us.codecraft.webmagic.model.annotation.ExtractBy;
import us.codecraft.webmagic.model.annotation.TargetUrl;

@TargetUrl("http://www.walmart.com/ip/\\d+")
public class Walmart implements AfterExtractor {


	@ExtractBy(value = "//ol[@itemprop='breadcrumb']//li[last()]/a/text()", configure=@ConfigInfo(defaultValue = ""))
	private String categroy;

	@ExtractBy(value = "//meta[@itemprop='brand']/@content")
	private String manufacturer;

	@ExtractBy(value = "//meta[@itemprop='model']/@content")
	private String mpn;

	@ComboExtract(value = 
			{ 
			@ExtractBy(value = "//meta[@itemprop='name']/@content", type = ExprType.XPATH),
			@ExtractBy(value = "h1.productTitle", type = ExprType.CSS, configure=@ConfigInfo(isOuterHtml = false)),
			@ExtractBy(value = "h1.productTitle p span", type = ExprType.CSS) 
			}, op = OP.OR)
	private String productName;

	@ComboExtract(value = 
			{ 
			@ExtractBy(value = "R3_ITEM\\.setId\\(['\"](\\d+)['\"]\\)", type = ExprType.REGEX),
			@ExtractBy(value = "var\\s+DefaultItem\\s*=\\s*\\{\\s*itemId\\s*:\\s*(\\d+)\\s*,", type = ExprType.REGEX),
			@ExtractBy(value = "//input[@name='product_id']/@value")
			}, op = OP.OR)
	private String productId;
	
	@ComboExtract(value =
			{
			@ExtractBy(value = "table.SpecTable", type = ExprType.CSS),
			@ExtractBy(value = "Walmart No\\.:</td>\\s*<td.+?>(\\d+)</td>", type = ExprType.REGEX)
			}, op = OP.AND)
	private String channelSKU;
	
	@ExtractBy(value = "div#UPC_MESSAGE strong#UPC_CODE", type = ExprType.CSS, configure=@ConfigInfo(isOuterHtml = false))
	private String upc;
	
	@ExtractBy(value = "http://content.webcollage.net/walmart/resources/content-player/v2/content-player.min.js", type = ExprType.CONTAINS, configure=@ConfigInfo(defaultValue="false"))
	private String wcPlayer;
	
	@ComboExtract(value = 
			{
			@ExtractBy(value = "http://content.webcollage.net/walmart/resources/content-player/v2/ppp.min.js", type = ExprType.CONTAINS,configure=@ConfigInfo(defaultValue="false")),
			@ExtractBy(value = "div#wc-aplus", type = ExprType.CSS)
			}, op = OP.OR)
	private String wcEmc;
	
	@ExtractBy(value = "a", type = ExprType.CSS, configure = @ConfigInfo(isRemoveTag=true), multi=true)
	private List<String> test;

	public Walmart() {
		//nothing
	}

	@Override
	public void afterProcess(Page page) {
		Map<String, String> map = page.getResultItems().getAllHttpHeaderResponses();
		Set<String> keys = map.keySet();
		for (String key : keys) {
			//System.out.println(key + ":" + map.get(key));
		}
	}

	public static void main(String[] args) {
		Set<Integer> acceptStatCode = new HashSet<Integer>();
		acceptStatCode.add(200);
		acceptStatCode.add(503);
		String domain = "www.walmart.com";
		String userAgent = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.8; rv:22.0) Gecko/20100101 Firefox/22.0";
		int sleepTime = 1000;
		int retryTimes = 3;
		Site walmartSite = Site.me().setDomain(domain).setAcceptStatCode(acceptStatCode).setUserAgent(userAgent)
				.setSleepTime(sleepTime).setRetryTimes(retryTimes);
		OOSpider.create(walmartSite, new WalmartPageModelPipeline(), Walmart.class).test(
				"http://www.walmart.com/ip/9886285");
	}

}
