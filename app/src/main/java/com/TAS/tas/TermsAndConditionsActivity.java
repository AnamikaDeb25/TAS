package com.TAS.tas;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.webkit.WebView;

public class TermsAndConditionsActivity extends AppCompatActivity {

    private WebView mWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms_and_conditions);

        mWebView = (WebView) findViewById(R.id.web_view);
        mWebView.getSettings().setJavaScriptEnabled(true);

        // Load the HTML content from a file or a web server
        String html = "<html>\n" +
                "  <head>\n" +
                "    <title>TAS Android Application Terms and Conditions</title>\n" +
                "  </head>\n" +
                "  <body>\n" +
                "    <h1>Terms and Conditions</h1>\n" +
                "    <p>Welcome to TAS, a social media platform where users can post pictures and chat with each other. Before using TAS, please read and agree to the following Terms and Conditions:</p>\n" +
                "    <ol>\n" +
                "      <li><strong>Acceptance of Terms:</strong> By downloading and using the TAS application, you agree to be bound by these Terms and Conditions. If you do not agree to these Terms and Conditions, please do not use the TAS application.</li>\n" +
                "      <li><strong>User Content:</strong> You are solely responsible for the content you post on TAS. You agree not to post any content that is illegal, obscene, defamatory, harassing, or infringes on the rights of others. TAS reserves the right to remove any content that violates these Terms and Conditions or is deemed inappropriate.</li>\n" +
                "      <li><strong>Intellectual Property:</strong> All content on TAS, including but not limited to text, images, graphics, and logos, is the property of TAS or its respective owners. You may not use any content from TAS without the prior written consent of TAS or its respective owners.</li>\n" +
                "      <li><strong>Privacy:</strong> TAS respects your privacy and is committed to protecting your personal information. Please refer to our Privacy Policy for more information on how we collect, use, and disclose your personal information.</li>\n" +
                "      <li><strong>User Conduct:</strong> You agree to use TAS in a lawful and respectful manner. You agree not to engage in any behavior that disrupts the use of TAS by other users, including but not limited to spamming, hacking, or uploading viruses or malware.</li>\n" +
                "      <li><strong>Limitation of Liability:</strong> TAS is not responsible for any damages, including but not limited to direct, indirect, incidental, or consequential damages, arising from the use or inability to use TAS.</li>\n" +
                "      <li><strong>Modification of Terms:</strong> TAS reserves the right to modify these Terms and Conditions at any time. Your continued use of TAS after any modifications indicates your acceptance of the modified Terms and Conditions.</li>\n" +
                "      <li><strong>Termination:</strong> TAS may terminate your use of TAS at any time for any reason, without notice.</li>\n" +
                "      <li><strong>Governing Law:</strong> These Terms and Conditions shall be governed by and construed in accordance with the laws of Tripura, India.</li>\n" +
                "    </ol>\n" +
                "    <p>By using TAS, you agree to these Terms and Conditions. If you have any questions or concerns, please contact us at kgt.anamika@gmail.com.</p>\n" +
                "  </body>\n" +
                "</html>";
        mWebView.loadDataWithBaseURL(null, html, "text/html", "UTF-8", null);
    }
}