package it.niedermann.owncloud.notes;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.util.TypedValue;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.PreferenceManager;

import io.noties.markwon.Markwon;
import io.noties.markwon.ext.strikethrough.StrikethroughPlugin;
import io.noties.markwon.ext.tables.TablePlugin;
import io.noties.markwon.ext.tasklist.TaskListPlugin;
import io.noties.markwon.html.HtmlPlugin;
import io.noties.markwon.image.ImagesPlugin;
import io.noties.markwon.linkify.LinkifyPlugin;
import it.niedermann.owncloud.notes.R;
import it.niedermann.owncloud.notes.branding.BrandedActivity;
import it.niedermann.owncloud.notes.databinding.ActivityFormattingHelpBinding;
import it.niedermann.owncloud.notes.editor.plugins.InternalLinksPlugin;
import it.niedermann.owncloud.notes.editor.plugins.ToggleableTaskListPlugin;

import static it.niedermann.owncloud.notes.shared.util.MarkDownUtil.CHECKBOX_CHECKED_MINUS;
import static it.niedermann.owncloud.notes.shared.util.MarkDownUtil.CHECKBOX_CHECKED_STAR;
import static it.niedermann.owncloud.notes.shared.util.MarkDownUtil.CHECKBOX_UNCHECKED_MINUS;
import static it.niedermann.owncloud.notes.shared.util.MarkDownUtil.CHECKBOX_UNCHECKED_STAR;
import static it.niedermann.owncloud.notes.shared.util.NoteUtil.getFontSizeFromPreferences;

public class FormattingHelpActivity extends BrandedActivity {

    private ActivityFormattingHelpBinding binding;
    private String content;

    private Markwon markwon;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityFormattingHelpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        content = buildFormattingHelp();

        markwon = Markwon.builder(this)
                .usePlugin(StrikethroughPlugin.create())
                .usePlugin(TablePlugin.create(this))
                .usePlugin(TaskListPlugin.create(this))
                .usePlugin(HtmlPlugin.create())
                .usePlugin(ImagesPlugin.create())
                .usePlugin(LinkifyPlugin.create())
                .usePlugin(new ToggleableTaskListPlugin(content, newCompleteContent -> {
                    content = newCompleteContent;
                    markwon.setMarkdown(binding.content, content);
                }))
//                .usePlugin(SyntaxHighlightPlugin.create(new Prism4j(new GrammarLocatorDef()), Prism4jThemeDefault.create()))
                .build();
        binding.content.setMovementMethod(LinkMovementMethod.getInstance());

        markwon.setMarkdown(binding.content, content);

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        binding.content.setTextSize(TypedValue.COMPLEX_UNIT_PX, getFontSizeFromPreferences(this, sp));
        if (sp.getBoolean(getString(R.string.pref_key_font), false)) {
            binding.content.setTypeface(Typeface.MONOSPACE);
        }
    }

    @NonNull
    private String buildFormattingHelp() {
        final String lineBreak = "\n";
        final String indention = "  ";
        final String divider = getString(R.string.formatting_help_divider);
        final String codefence = getString(R.string.formatting_help_codefence);

        int numberedListItem = 1;
        final String lists = getString(R.string.formatting_help_lists_body_1) + lineBreak +
                lineBreak +
                getString(R.string.formatting_help_ol, numberedListItem++, getString(R.string.formatting_help_lists_body_2)) + lineBreak +
                getString(R.string.formatting_help_ol, numberedListItem++, getString(R.string.formatting_help_lists_body_3)) + lineBreak +
                getString(R.string.formatting_help_ol, numberedListItem, getString(R.string.formatting_help_lists_body_4)) + lineBreak +
                lineBreak +
                getString(R.string.formatting_help_lists_body_5) + lineBreak +
                lineBreak +
                getString(R.string.formatting_help_ul, getString(R.string.formatting_help_lists_body_6)) + lineBreak +
                getString(R.string.formatting_help_ul, getString(R.string.formatting_help_lists_body_7)) + lineBreak +
                indention + getString(R.string.formatting_help_ul, getString(R.string.formatting_help_lists_body_8)) + lineBreak +
                indention + getString(R.string.formatting_help_ul, getString(R.string.formatting_help_lists_body_9)) + lineBreak;

        final String checkboxes = getString(R.string.formatting_help_checkboxes_body_1) + lineBreak +
                lineBreak +
                getString(R.string.formatting_help_checkbox_checked, getString(R.string.formatting_help_checkboxes_body_2)) + lineBreak +
                getString(R.string.formatting_help_checkbox_unchecked, getString(R.string.formatting_help_checkboxes_body_3)) + lineBreak;

        final String structuredDocuments = getString(R.string.formatting_help_structured_documents_body_1, "`#`", "`##`") + lineBreak +
                lineBreak +
                getString(R.string.formatting_help_title_level_3, getString(R.string.formatting_help_structured_documents_body_2)) + lineBreak +
                lineBreak +
                getString(R.string.formatting_help_structured_documents_body_3, "`#`", "`######`") + lineBreak +
                lineBreak +
                getString(R.string.formatting_help_structured_documents_body_4, getString(R.string.formatting_help_quote_keyword)) + lineBreak +
                lineBreak +
                getString(R.string.formatting_help_quote, getString(R.string.formatting_help_structured_documents_body_5)) + lineBreak +
                getString(R.string.formatting_help_quote, getString(R.string.formatting_help_structured_documents_body_6)) + lineBreak;

        final String javascript = getString(R.string.formatting_help_javascript_1) + lineBreak +
                indention + indention + getString(R.string.formatting_help_javascript_2) + lineBreak +
                getString(R.string.formatting_help_javascript_3) + lineBreak;

        return getString(R.string.formatting_help_title, getString(R.string.formatting_help_cbf_title)) + lineBreak +
                lineBreak +
                getString(R.string.formatting_help_cbf_body_1) + lineBreak +
                getString(R.string.formatting_help_cbf_body_2,
                        getString(R.string.formatting_help_codefence_inline, getString(android.R.string.cut)),
                        getString(R.string.formatting_help_codefence_inline, getString(android.R.string.copy)),
                        getString(R.string.formatting_help_codefence_inline, getString(android.R.string.selectAll)),
                        getString(R.string.formatting_help_codefence_inline, getString(R.string.simple_link)),
                        getString(R.string.formatting_help_codefence_inline, getString(R.string.simple_checkbox))
                ) + lineBreak +
                lineBreak +
                divider + lineBreak +
                lineBreak +
                getString(R.string.formatting_help_title, getString(R.string.formatting_help_text_title)) + lineBreak +
                lineBreak +
                getString(R.string.formatting_help_text_body,
                        getString(R.string.formatting_help_bold),
                        getString(R.string.formatting_help_italic),
                        getString(R.string.formatting_help_strike_through)
                ) + lineBreak +
                lineBreak +
                codefence + lineBreak +
                getString(R.string.formatting_help_text_body,
                        getString(R.string.formatting_help_bold),
                        getString(R.string.formatting_help_italic),
                        getString(R.string.formatting_help_strike_through)
                ) + lineBreak +
                codefence + lineBreak +
                lineBreak +
                divider + lineBreak +
                lineBreak +
                getString(R.string.formatting_help_title, getString(R.string.formatting_help_lists_title)) + lineBreak +
                lineBreak +
                lists +
                lineBreak +
                codefence + lineBreak +
                lists +
                codefence + lineBreak +
                lineBreak +
                divider + lineBreak +
                lineBreak +
                getString(R.string.formatting_help_title, getString(R.string.formatting_help_checkboxes_title)) + lineBreak +
                lineBreak +
                checkboxes +
                lineBreak +
                codefence + lineBreak +
                checkboxes +
                codefence + lineBreak +
                lineBreak +
                divider + lineBreak +
                lineBreak +
                getString(R.string.formatting_help_title, getString(R.string.formatting_help_structured_documents_title)) + lineBreak +
                lineBreak +
                structuredDocuments +
                lineBreak +
                codefence + lineBreak +
                structuredDocuments +
                codefence + lineBreak +
                lineBreak +
                divider + lineBreak +
                lineBreak +
                getString(R.string.formatting_help_title, getString(R.string.formatting_help_code_title)) + lineBreak +
                lineBreak +
                getString(R.string.formatting_help_code_body_1) + lineBreak +
                lineBreak +
                getString(R.string.formatting_help_codefence_inline_escaped, getString(R.string.formatting_help_code_javascript_inline)) + lineBreak +
                getString(R.string.formatting_help_codefence_inline, getString(R.string.formatting_help_code_javascript_inline)) + lineBreak +
                lineBreak +
                getString(R.string.formatting_help_code_body_2) + lineBreak +
                lineBreak +
                getString(R.string.formatting_help_codefence_escaped) + lineBreak +
                javascript +
                getString(R.string.formatting_help_codefence_escaped) + lineBreak +
                lineBreak +
                codefence + lineBreak +
                javascript +
                codefence + lineBreak +
                lineBreak +
                getString(R.string.formatting_help_code_body_3) + lineBreak +
                lineBreak +
                getString(R.string.formatting_help_codefence_javascript_escaped) + lineBreak +
                javascript +
                getString(R.string.formatting_help_codefence_escaped) + lineBreak +
                lineBreak +
                getString(R.string.formatting_help_codefence_javascript) + lineBreak +
                javascript +
                codefence + lineBreak +
                lineBreak +
                divider + lineBreak +
                lineBreak +
                getString(R.string.formatting_help_title, getString(R.string.formatting_help_unsupported_title)) + lineBreak +
                lineBreak +
                getString(R.string.formatting_help_unsupported_body_1) + lineBreak +
                lineBreak +
                getString(R.string.formatting_help_ul, getString(R.string.formatting_help_unsupported_body_2)) + lineBreak +
                getString(R.string.formatting_help_ul, getString(R.string.formatting_help_unsupported_body_3)) + lineBreak +
                lineBreak +
                getString(R.string.formatting_help_unsupported_body_4) + lineBreak;
    }

    @Override
    public void applyBrand(int mainColor, int textColor) {
        applyBrandToPrimaryToolbar(binding.appBar, binding.toolbar);
    }
}
