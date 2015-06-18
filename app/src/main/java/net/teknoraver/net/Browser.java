package net.teknoraver.teknobrowser;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.http.SslError;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Window;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public class Browser extends Activity implements TextView.OnEditorActionListener {
	private EditText et;
	private WebView wv;

	@SuppressLint("SetJavaScriptEnabled")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().requestFeature(Window.FEATURE_PROGRESS);

		setContentView(R.layout.activity_browser);

		et = (EditText) findViewById(R.id.url);
		wv = (WebView) findViewById(R.id.webView);

		et.setOnEditorActionListener(this);
		wv.getSettings().setJavaScriptEnabled(true);
		wv.setWebChromeClient(new WebChromeClient() {
			@Override
			public void onProgressChanged(final WebView view, final int progress) {
				setProgress(progress * 100);
			}
		});
		wv.setWebViewClient(new WebViewClient() {
			@Override
			public void onReceivedError(final WebView view, final int errorCode, final String description, final String failingUrl) {
				Toast.makeText(Browser.this, description, Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onPageStarted(final WebView view, final String url, final Bitmap favicon) {
				if(url.startsWith("https://")) {
					et.getBackground().setColorFilter(Color.GREEN, PorterDuff.Mode.SRC_ATOP);
				} else
					et.getBackground().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
				et.setText(url);
			}

			@Override
			public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
				et.getBackground().setColorFilter(Color.RED, PorterDuff.Mode.SRC_ATOP);
				handler.proceed();
			}
		});

		final Intent intent = getIntent();
		final String action = intent.getAction();
		if (Intent.ACTION_VIEW.equals(action)) {
			wv.loadUrl(intent.getData().toString());
		}
	}

	@Override
	public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
		String url = et.getText().toString();
		if(!(url.startsWith("http://") || url.startsWith("https://"))) {
			if(url.matches(".*\\....*"))
				url = "http://" + url;
			else
				url = "https://www.google.com/search?q=" + url;
			et.setText(url, TextView.BufferType.EDITABLE);
		}
		wv.loadUrl(url);
		return false;
	}

	@Override
	public boolean onKeyDown(final int keyCode, final KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK && wv.canGoBack()) {
			wv.goBack();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}
