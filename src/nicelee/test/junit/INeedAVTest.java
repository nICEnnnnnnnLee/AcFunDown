package nicelee.test.junit;


import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import nicelee.acfun.downloaders.Downloader;
import nicelee.acfun.model.VideoInfo;
import nicelee.acfun.parsers.InputParser;
import nicelee.acfun.util.HttpRequestUtil;
import nicelee.ui.Global;

public class INeedAVTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	/**
	 * 测试根据ac号获取信息
	 * https://www.acfun.cn/v/ac10187818
	 */
	//@Test
	public void testGetAvInfo() {
		HttpRequestUtil util = new HttpRequestUtil();
		Downloader downloader = new Downloader();
		downloader.init(util);
		//AVParser parser = new AVParser(util, Global.pageSize, Global.pageDisplay);
		InputParser inputParser = new InputParser(util, Global.pageSize, Global.pageDisplay);
		inputParser.result("ac10656289", 0, true);
	}
	
	/**
	 * 测试根据ab号获取信息
	 * ab5024869_34168_327107
	 */
	//@Test
	public void testGetABInfo() {
		HttpRequestUtil util = new HttpRequestUtil();
		Downloader downloader = new Downloader();
		downloader.init(util);
		//AVParser parser = new AVParser(util, Global.pageSize, Global.pageDisplay);
		InputParser inputParser = new InputParser(util, Global.pageSize, Global.pageDisplay);
		inputParser.result("ab5024869_34168_327107", 0, true);
	}
	
	/**
	 * 测试根据ab号获取信息
	 * ab5024869_34168_327107
	 */
	@Test
	public void testDownABInfo() {
		HttpRequestUtil util = new HttpRequestUtil();
		Downloader downloader = new Downloader();
		downloader.init(util);
		//AVParser parser = new AVParser(util, Global.pageSize, Global.pageDisplay);
		InputParser inputParser = new InputParser(util, Global.pageSize, Global.pageDisplay);
		VideoInfo video = inputParser.result("ab5024869_34168_327107", 0, true);
		
		downloader.download(video.getClips().get(10441665L).getLinks().get(3), 
				"ab5024869_34168_327107", 3, 0);
	}
	
	//@Test
	public void test() {
		Pattern pCidInfo = Pattern.compile("data-href='([^>]*?)' title=\"([^>]*?)\" data-id='10207415'");
		Matcher matcher = pCidInfo.matcher("data-href='/v/ac10187818_1' title=\"01\" data-id='10197322'>01</span><span class='single-p' data-href='/v/ac10187818_2' title=\"02\" data-id='10207415'>02</span><span class='single-p' data-href='/v/ac10187818_3' title=\"03\" data-id='10205814'>03</span><span class='single-p' data-href='/v/ac10187818_4' title=\"04\" data-id='10205816'>04</span><span class='single-p' data-href='/v/ac10187818_5' title=\"05\" data-id='10205818'>05</span><span class='single-p' data-href='/v/ac10187818_6' title=\"06\" data-id='10205820'>06</span><span class='single-p' data-href='/v/ac10187818_7' title=\"07\" data-id='10205819'>07</span><span class='single-p' data-href='/v/ac10187818_8' title=\"08\" data-id='10205821'>08</span><span class='single-p' data-href='/v/ac10187818_9' title=\"09\" data-id='10205823'>09</span><span class='single-p' data-href='/v/ac10187818_10' title=\"10\" data-id='10205824'>10</span><span class='single-p' data-href='/v/ac10187818_11' title=\"11\" data-id='10205817'>11</span><span class='single-p' data-href='/v/ac10187818_12' title=\"12\" data-id='10207414'>12</span><span class='single-p' data-href='/v/ac10187818_13' title=\"13\" data-id='10207418'>13</span><span class='single-p' data-href='/v/ac10187818_14' title=\"14\" data-id='10207884'>14</span><span class='single-p' data-href='/v/ac10187818_15' title=\"15\" data-id='10207419'>15</span><span class='single-p' data-href='/v/ac10187818_16' title=\"16\" data-id='10207420'>16</span><span class='single-p' data-href='/v/ac10187818_17' title=\"17\" data-id='10207425'>17</span><span class='single-p' data-href='/v/ac10187818_18' title=\"18\" data-id='10207426'>18</span><span class='single-p' data-href='/v/ac10187818_19' title=\"19\" data-id='10207428'>19</span><span class='single-p' data-href='/v/ac10187818_20' title=\"20\" data-id='10207429'>20</span><span class='single-p' data-href='/v/ac10187818_21' title=\"21\" data-id='10207682'>21</span><span class='single-p' data-href='/v/ac10187818_22' title=\"22\" data-id='10207683'>22</span><span class='single-p' data-href='/v/ac10187818_23' title=\"23\" data-id='10207684'>23</span><span class='single-p' data-href='/v/ac10187818_24' title=\"24\" data-id='10207686'>24</span><span class='single-p' data-href='/v/ac10187818_25' title=\"25\" data-id='10207687'>25</span><span class='single-p' data-href='/v/ac10187818_26' title=\"26\" data-id='10207902'>26</span><span class='single-p' data-href='/v/ac10187818_27' title=\"27\" data-id='10207903'>27</span><span class='single-p' data-href='/v/ac10187818_28' title=\"28\" data-id='10207904'>28</span><span class='single-p' data-href='/v/ac10187818_29' title=\"29\" data-id='10207905'>29</span><span class='single-p' data-href='/v/ac10187818_30' title=\"30\" data-id='10207906'>30</span><span class='single-p' data-href='/v/ac10187818_31' title=\"31\" data-id='10207951'>31</span><span class='single-p' data-href='/v/ac10187818_32' title=\"32\" data-id='10207950'>32</span><span class='single-p' data-href='/v/ac10187818_33' title=\"33\" data-id='10207952'>33</span><span class='single-p' data-href='/v/ac10187818_34' title=\"34\" data-id='10207953'>34</span><span class='single-p' data-href='/v/ac10187818_35' title=\"35\" data-id='10207954'>35</span><span class='single-p' data-href='/v/ac10187818_36' title=\"36\" data-id='10207934'>36</span><span class='single-p' data-href='/v/ac10187818_37' title=\"37\" data-id='10207930'>37</span><span class='single-p' data-href='/v/ac10187818_38' title=\"38\" data-id='10207935'>38</span><span class='single-p' data-href='/v/ac10187818_39' title=\"39\" data-id='10207707'>39</span><span class='single-p' data-href='/v/ac10187818_40' title=\"40\" data-id='10207931'>40</span><span class='single-p' data-href='/v/ac10187818_41' title=\"41\" data-id='10208260'>41</span><span class='single-p' data-href='/v/ac10187818_42' title=\"42\" data-id='10208261'>42</span><span class='single-p' data-href='/v/ac10187818_43' title=\"43\" data-id='10208256'>43</span><span class='single-p' data-href='/v/ac10187818_44' title=\"44\" data-id='10208257'>44</span><span class='single-p' data-href='/v/ac10187818_45' title=\"45\" data-id='10208262'>45</span><span class='single-p' data-href='/v/ac10187818_46' title=\"46\" data-id='10208282'>46</span><span class='single-p' data-href='/v/ac10187818_47' title=\"47\" data-id='10208312'>47</span><span class='single-p' data-href='/v/ac10187818_48' title=\"48\" data-id='10208288'>48</span><span class='single-p' data-href='/v/ac10187818_49' title=\"49\" data-id='10208286'>49</span><span class='single-p' data-href='/v/ac10187818_50' title=\"50\" data-id='10208292'>50</span><span class='single-p' data-href='/v/ac10187818_51' title=\"51\" data-id='10208339'>51</span><span class='single-p' data-href='/v/ac10187818_52' title=\"52\" data-id='10208340'>52</span><span class='single-p' data-href='/v/ac10187818_53' title=\"53\" data-id='10208463'>53</span><span class='single-p' data-href='/v/ac10187818_54' title=\"54\" data-id='10208443'>54</span><span class='single-p' data-href='/v/ac10187818_55' title=\"55\" data-id='10208336'>55</span><span class='single-p' data-href='/v/ac10187818_56' title=\"56\" data-id='10208503'>56</span><span class='single-p' data-href='/v/ac10187818_57' title=\"57\" data-id='10208498'>57</span><span class='single-p' data-href='/v/ac10187818_58' title=\"58\" data-id='10208506'>58</span><span class='single-p' data-href='/v/ac10187818_59' title=\"59\" data-id='10208507'>59</span><span class='single-p' data-href='/v/ac10187818_60' title=\"60\" data-id='10208520'>60</span></div></div><div class='fr part-ctrl'><span class='open fl'>展开共60Part视频&nbsp;∨ </span><span class='close fl'>收起共60Part视频&nbsp;∧</span></div></section>");
		matcher.find();
		System.out.println(matcher.group(1));
		System.out.println(matcher.group(2));
	}
//	public void testGetAvInfo() {
//		INeedAV avs = new INeedAV();
//		VideoInfo video = null;
//		video = avs.getVideoDetail("av35296336", Global.downloadFormat, true);
//		if (!video.getAuthorId().equals("179497530")) {
//			fail("AuthorId Not Expected");
//		}
//		if (!video.getVideoName().contains("【炮姐】某科学的超电磁炮op合集  【无字幕版】")) {
//			fail("VideoName Not Expected");
//		}
//		if (!video.getVideoPreview()
//				.equals("http://i1.hdslb.com/bfs/archive/b04c8df57dea36b283865fca73be6a198e21c94c.jpg")) {
//			fail("VideoPreview Link Not Expected");
//		}
//		ClipInfo clip = video.getClips().get(61860101L);
//		if (clip == null) {
//			fail("clip is null");
//		}
//		if (!clip.getLinks().get(32).contains("/61860101/61860101-1-32.flv")
//				&& !clip.getLinks().get(32).contains("/61860101/61860101-1-30032.m4s")) {
//			fail("Video Link Not Expected");
//		}
//	}

}
