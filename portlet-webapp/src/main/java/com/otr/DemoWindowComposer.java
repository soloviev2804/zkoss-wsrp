package com.otr;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.MouseEvent;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Button;
import org.zkoss.zul.Filedownload;
import org.zkoss.zul.Image;
import org.zkoss.zul.Label;

import javax.portlet.PortletRequest;
import javax.servlet.http.HttpServletRequest;
import java.io.FileNotFoundException;

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
		Filedownload.save("plain text string".getBytes(),"text/plain","text.txt");
	}
}