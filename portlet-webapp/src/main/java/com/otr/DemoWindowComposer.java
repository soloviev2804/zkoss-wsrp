package com.otr;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.MouseEvent;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Button;
import org.zkoss.zul.Filedownload;
import org.zkoss.zul.Image;
import org.zkoss.zul.Label;

import java.util.Date;

public class DemoWindowComposer extends GenericForwardComposer {

	private Label lb;
	private Button showImgBtn;
	private Image picture;
	
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
	}

	public void onClick$btn(MouseEvent event) {
		lb.setValue("Hello " + System.currentTimeMillis());
	}

	public void onClick$showImgBtn(MouseEvent event) {
		picture.setSrc("1.jpg");
	}

	public void onClick$downloadBtn(MouseEvent event) {
		Filedownload.save(("plain text string" + new Date()).getBytes(), "text/plain", "text" + System.currentTimeMillis() + ".txt");
	}
}