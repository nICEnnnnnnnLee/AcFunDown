package nicelee.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import nicelee.acfun.INeedAV;
import nicelee.acfun.model.FavList;
import nicelee.ui.item.MJTextField;
import nicelee.ui.thread.GetVideoDetailThread;
import nicelee.ui.thread.LoginThread;

public class TabIndex extends JPanel implements ActionListener, MouseListener, ItemListener{

	/**
	 * 
	 */
	private static final long serialVersionUID = -5829023045158490349L;
	public ImageIcon imgIconHeaderDefault = new ImageIcon(this.getClass().getResource("/resources/header.png"));
	public ImageIcon backgroundIcon = Global.backgroundImg;
	public JLabel jlHeader;
	String placeHolder = "请在此输入A站链接或 ac/ab/aa号...";
	JTextField txtSearch = new MJTextField(placeHolder);
	//new MJTextField("https://www.bilibili.com/video/av35296336");
	JButton btnSearch = new JButton("查找");
	JButton btnSearchNextPage = new JButton("下一页");
	public JComboBox<Object> cbChannel; // 分区
	
	JTextArea consoleArea = new JTextArea(20, 50);
	JTabbedPane jTabbedpane;

	public TabIndex(JTabbedPane jTabbedpane) {
		this.jTabbedpane = jTabbedpane;
		init();
	}

	public void init() {
		this.setPreferredSize(new Dimension(1150, 620));
//		OperationPanel operPanel = new OperationPanel();
//		this.add(operPanel);
		
//		btnUpdate = new JLabel("<html>更新<br/>版本</html>", JLabel.CENTER);
//		btnUpdate.setOpaque(true);
//		btnUpdate.setPreferredSize(new Dimension(80, 80));
//		btnUpdate.setFont(new Font("黑体", Font.BOLD, 30));
//		btnUpdate.addMouseListener(this);
//		this.add(btnUpdate);
		
		// 空白模块- 占位
		JLabel jpBLANK = new JLabel();
		jpBLANK.setPreferredSize(new Dimension(920, 80));
		this.add(jpBLANK);
		
		// 空白模块- 占位
		imgIconHeaderDefault = new ImageIcon(imgIconHeaderDefault.getImage().getScaledInstance(80, 80, Image.SCALE_SMOOTH));
		jlHeader = new JLabel(imgIconHeaderDefault);
		jlHeader.addMouseListener(this);
		this.add(jlHeader);
		
		//头像模块
		URL fileURL = this.getClass().getResource("/resources/title.png");
		ImageIcon imgIcon = new ImageIcon(fileURL);
		//imgIcon = new ImageIcon(imgIcon.getImage().getScaledInstance(350, 350, Image.SCALE_SMOOTH));
		JLabel jLabel = new JLabel(imgIcon);
		this.add(jLabel);
		
		// 查找模块
		JPanel jpSearch = new JPanel();
		txtSearch.setPreferredSize(new Dimension(700, 40));
		btnSearch.addActionListener(this);
		btnSearch.setPreferredSize(new Dimension(90, 40));
		btnSearchNextPage.addActionListener(this);
		btnSearchNextPage.setPreferredSize(new Dimension(90, 40));
		
		cbChannel = new JComboBox<>();
		cbChannel.addItem("---我的收藏夹---");
		cbChannel.setSelectedIndex(0);
		// cbChannel.addActionListener(this);
		cbChannel.addItemListener(this);
		cbChannel.setPreferredSize(new Dimension(120, 40));
		
		jpSearch.add(txtSearch);
		jpSearch.add(btnSearch);
		jpSearch.add(btnSearchNextPage);
		jpSearch.add(cbChannel);
		jpSearch.setOpaque(false);
		this.add(jpSearch);
		this.setOpaque(false);
	}
	
	/**
	 * 关闭所有视频Tab
	 */
	public void closeAllVideoTabs() {
		System.out.println("当前Tab数量： " + (jTabbedpane.getTabCount() - 2));
		System.out.println("正在关闭Tab标签页");
		for(int i = jTabbedpane.getTabCount() - 1; i >= 2 ; i--) {
			jTabbedpane.removeTabAt(i);
		}
		System.out.println("当前Tab数量： " + (jTabbedpane.getTabCount() - 2));
	}
	
	/**
	 * 根据需要下载所有打开的Tab页视频
	 * @param downAll
	 * @param qn
	 */
	public void downVideoTabs(boolean downAll, int qn) {
		for(int i = 0; i < jTabbedpane.getTabCount(); i++) {
			//判断是否为Video标签页, 是就下载
			System.out.printf("Tab 页共 %d 个，当前第 %d 个\r\n",
					jTabbedpane.getTabCount(),
					i);
			Component comp = jTabbedpane.getComponentAt(i);
			if(comp instanceof TabVideo ) {
				TabVideo tabVideo = (TabVideo) comp;
				tabVideo.download(downAll, qn);
			}
		}
	}
	@Override
	public void paintComponent(Graphics g) {
//		// super.paintComponent(g);
		Image img = backgroundIcon.getImage();
		int width = img.getWidth(this.getParent());
		int height = img.getHeight(this.getParent());
		int xGap = 5;
		int xCnt = this.getSize().width / (width + xGap) + 1;
		int yGap = 5;
		int yCnt = this.getSize().height / (height + yGap) + 1;
		if( xCnt >= 3) {
			for(int x = 0; x <= xCnt; x++) {
				int xp = xGap + (width + xGap) * x;
				for(int y = 0; y < yCnt; y++) {
					int yp = yGap + (height + yGap) * y;
					g.drawImage(backgroundIcon.getImage(), xp, yp, width, height, this.getParent());
				}
			}
		}else {
			g.drawImage(backgroundIcon.getImage(), 0, 0, this.getSize().width, this.getSize().height, this.getParent());
		}
		this.setOpaque(false);
	}
	
	/**
	 * 对应 查找 按钮的点击事件
	 */
	protected final static Pattern pagePattern = Pattern.compile("p=([0-9]+)");// 自定义参数, 目前只匹配个人主页视频的页码
	protected final static Pattern channelIdPattern = Pattern.compile("channelId=([0-9]+)");
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btnSearch) {
			search();
		}else if(e.getSource() == btnSearchNextPage){
			String origin = txtSearch.getText();
			int page = 1;
			String modified = null;
			Matcher matcher = pagePattern.matcher(origin);
			if(matcher.find()) {
				page = Integer.parseInt(matcher.group(1));
				modified = origin.replaceFirst("p=[0-9]+", "p=" + (page+1));
			}else {
				modified = origin + "&p=" + (page+1);
			}
			txtSearch.setText(modified);
			search();
		}
//		else  if(e.getSource() == cbChannel){
//			// 找到分区id
//			int channelId = ChannelEnum.getChannelId(cbChannel.getSelectedItem().toString());
//			
//			// 分情况对变量进行修饰
//			String origin = txtSearch.getText();
//			String modified = null;
//			Matcher matcher = channelIdPattern.matcher(origin);
//			if(matcher.find()) {
//				modified = origin.replaceFirst("channelId=[0-9]+", "channelId=" + channelId);
//			}else {
//				modified = origin + "&channelId=" + channelId;
//			}
//			txtSearch.setText(modified);
//		}
	}

	@Override
	public void itemStateChanged(ItemEvent e) {
		if (e.getStateChange() == ItemEvent.SELECTED) {
            if(e.getItem() instanceof FavList) {
            	FavList fav = (FavList) e.getItem();
            	String url = "https://www.acfun.cn/member/favourite/folder/%d";
            	url = String.format(url, fav.getfId());
            	txtSearch.setText(url);
            	txtSearch.setForeground(Color.BLACK);
    			search();
            }
        }
	}
	
	/**
	 * 根据输入查找 av信息，并弹出av信息的Tab页
	 */
	public void search() {
		String avId = txtSearch.getText();
		if(!placeHolder.equals(avId)) {
			INeedAV iNeedAV = new INeedAV();
			avId = iNeedAV.getValidID(avId);
			System.out.println("当前解析的id为：");
			System.out.println(avId);
			popVideoInfoTab(avId);
//			if(avId.contains(" ")) {
//				String avs[] = avId.trim().split(" ");
//				System.out.println("将弹出窗口个数： " + avs.length);
//				for(String av : avs) {
//					popVideoInfoTab(av);
//				}
//			}else {
//				popVideoInfoTab(avId);
//			}
		}
		
	}

	/**
	 * 弹出avId对应的Video 标签页
	 * @param avId
	 */
	private void popVideoInfoTab(String avId) {
		if("".equals(avId)) {
			JOptionPane.showMessageDialog(this, "解析链接失败!", "失败", JOptionPane.WARNING_MESSAGE);
			return;
		}
		// 作品页
		JLabel label = new JLabel("正在加载中...");
		final TabVideo tab = new TabVideo(label);
		jTabbedpane.addTab("作品页", tab);
		jTabbedpane.setTabComponentAt(jTabbedpane.indexOfComponent(tab), label);
		GetVideoDetailThread th = new GetVideoDetailThread(tab, avId);
		label.addMouseListener(new MouseListener() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() >= 2) {
					jTabbedpane.remove(tab);
				} else {
					jTabbedpane.setSelectedComponent(tab);
				}
			}
			@Override
			public void mousePressed(MouseEvent e) {
			}
			@Override
			public void mouseReleased(MouseEvent e) {
			}
			@Override
			public void mouseEntered(MouseEvent e) {
			}
			@Override
			public void mouseExited(MouseEvent e) {
			}
		});
		th.start();
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if(e.getSource() == jlHeader) {
			Global.needToLogin = true;
			LoginThread loginTh = new LoginThread();
			loginTh.start();
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {
		if(e.getSource() == jlHeader) {
			jlHeader.setBorder(BorderFactory.createLineBorder(Color.red));
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if(e.getSource() == jlHeader) {
			jlHeader.setBorder(null);
		}
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
		
	}
}
