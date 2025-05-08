package chapter6.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;

import chapter6.beans.Message;
import chapter6.logging.InitApplication;
import chapter6.service.MessageService;

@WebServlet(urlPatterns = { "/edit" })
public class EditServlet extends HttpServlet {

	/**
	* ロガーインスタンスの生成
	*/
	Logger log = Logger.getLogger("twitter");

	/**
	* デフォルトコンストラクタ
	* アプリケーションの初期化を実施する。
	*/
	public EditServlet() {
		InitApplication application = InitApplication.getInstance();
		application.init();
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {

		log.info(new Object() { }.getClass().getEnclosingClass().getName() +
			" : " + new Object() { }.getClass().getEnclosingMethod().getName());

		String id = request.getParameter("id");

		Pattern p1 = Pattern.compile("^[0-9]+$"); // 正規表現パターンの読み込み
	    Matcher m1 = p1.matcher(id); // パターンと検査対象文字列の照合
	    boolean result = m1.matches(); // 照合結果をtrueまたはfalseで取得する

	    HttpSession session = request.getSession();
		List<String> errorMessages = new ArrayList<String>();

		Message message = new MessageService().getMessage(id);

		if (!result || message == null) {
			errorMessages.add("不正なパラメータが入力されました");
	    	session.setAttribute("errorMessages", errorMessages);
			response.sendRedirect("./");
			return;
		}

		request.setAttribute("message", message);
		request.setAttribute("text", message.getText());
		request.getRequestDispatcher("/edit.jsp").forward(request, response);
	}
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {

		log.info(new Object() { }.getClass().getEnclosingClass().getName() +
			" : " + new Object() { }.getClass().getEnclosingMethod().getName());

		List<String> errorMessages = new ArrayList<String>();
		String id = request.getParameter("id");
		String text = request.getParameter("text");

		if (!isValid(text, errorMessages,id)) {
			request.setAttribute("errorMessages", errorMessages);
			request.setAttribute("text", text);
			request.getRequestDispatcher("edit.jsp").forward(request, response);
			return;
		}

		Message message = new Message();
		message.setText(text);

		message.setId(id);

		new MessageService().edit(message);
		response.sendRedirect("./");
	}

	private boolean isValid(String text, List<String> errorMessages, String id) {

		log.info(new Object() { }.getClass().getEnclosingClass().getName() +
			" : " + new Object() { }.getClass().getEnclosingMethod().getName());

		if (StringUtils.isBlank(text)) {
			errorMessages.add("メッセージを入力してください");
		} else if (140 < text.length()) {
			errorMessages.add("140文字以下で入力してください");
		}

		if (errorMessages.size() != 0) {
			return false;
		}
		return true;
	}
}