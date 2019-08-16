package xhj.cn.start;

import io.vertx.core.Vertx;
import zyxhj.core.controller.TestController;
import zyxhj.core.controller.UserController;
import zyxhj.core.service.TagService;
import zyxhj.flow.service.AnnexService;
import zyxhj.flow.service.FlowService;
import zyxhj.utils.Singleton;
import zyxhj.utils.ZeroVerticle;

public class MainVerticle extends ZeroVerticle {

	public static void main(String[] args) {
		Vertx vertx = Vertx.vertx();
		vertx.deployVerticle(new MainVerticle());
	}

	public String name() {
		return "zero";
	}

	public int port() {
		return 8053;
	}

	protected void init() throws Exception {

		initCtrl(ctrlMap, Singleton.ins(TestController.class, "test"));

		initCtrl(ctrlMap, Singleton.ins(UserController.class, "user"));

		initCtrl(ctrlMap, Singleton.ins(TagService.class, "tag"));

		initCtrl(ctrlMap, Singleton.ins(AnnexService.class, "annex"));

		initCtrl(ctrlMap, Singleton.ins(FlowService.class, "flow"));

	}

}
