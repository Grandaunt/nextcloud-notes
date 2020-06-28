package it.niedermann.owncloud.notes.shared.util;

import androidx.annotation.NonNull;

/**
 * Created by stefan on 07.12.16.
 */

@SuppressWarnings("WeakerAccess")
public class MarkDownUtil {

    private static final String TAG = MarkDownUtil.class.getSimpleName();

    public static final String CHECKBOX_UNCHECKED_MINUS = "- [ ]";
    public static final String CHECKBOX_UNCHECKED_MINUS_TRAILING_SPACE = CHECKBOX_UNCHECKED_MINUS + " ";
    public static final String CHECKBOX_UNCHECKED_STAR = "* [ ]";
    public static final String CHECKBOX_UNCHECKED_STAR_TRAILING_SPACE = CHECKBOX_UNCHECKED_STAR + " ";
    public static final String CHECKBOX_CHECKED_MINUS = "- [x]";
    public static final String CHECKBOX_CHECKED_STAR = "* [x]";

//    /**
//     * Ensures every instance of RxMD uses the same configuration
//     *
//     * @param context Context
//     * @return RxMDConfiguration
//     */
//    public static Builder getMarkDownConfiguration(Context context) {
//        return getMarkDownConfiguration(context, Notes.isDarkThemeActive(context));
//    }
//
//    public static Builder getMarkDownConfiguration(Context context, Boolean darkTheme) {
//        return new MarkdownConfiguration.Builder(context)
//                .setUnOrderListColor(ResourcesCompat.getColor(context.getResources(),
//                        darkTheme ? R.color.widget_fg_dark_theme : R.color.widget_fg_default, null))
//                .setHeader2RelativeSize(1.35f)
//                .setHeader3RelativeSize(1.25f)
//                .setHeader4RelativeSize(1.15f)
//                .setHeader5RelativeSize(1.1f)
//                .setHeader6RelativeSize(1.05f)
//                .setHorizontalRulesHeight(2)
//                .setCodeBgColor(darkTheme ? ResourcesCompat.getColor(context.getResources(), R.color.fg_default_high, null) : Color.LTGRAY)
//                .setTheme(darkTheme ? new ThemeSonsOfObsidian() : new ThemeDefault())
//                .setTodoColor(ResourcesCompat.getColor(context.getResources(),
//                        darkTheme ? R.color.widget_fg_dark_theme : R.color.widget_fg_default, null))
//                .setTodoDoneColor(ResourcesCompat.getColor(context.getResources(),
//                        darkTheme ? R.color.widget_fg_dark_theme : R.color.widget_fg_default, null))
//                .setLinkFontColor(ResourcesCompat.getColor(context.getResources(), R.color.primary, null))
//                .setRxMDImageLoader(new NotesImageLoader(context))
//                .setDefaultImageSize(400, 300);
//    }
//
//    public static boolean containsImageSpan(@NonNull CharSequence text) {
//        return ((Spanned) text).getSpans(0, text.length(), MDImageSpan.class).length > 0;
//    }

    public static boolean lineStartsWithCheckbox(@NonNull String line) {
        return lineStartsWithCheckbox(line, true) || lineStartsWithCheckbox(line, false);
    }

    public static boolean lineStartsWithCheckbox(@NonNull String line, boolean starAsLeadingCharacter) {
        return starAsLeadingCharacter
                ? line.startsWith(CHECKBOX_UNCHECKED_STAR) || line.startsWith(CHECKBOX_CHECKED_STAR)
                : line.startsWith(CHECKBOX_UNCHECKED_MINUS) || line.startsWith(CHECKBOX_CHECKED_MINUS);
    }

    public static int getStartOfLine(@NonNull CharSequence s, int cursorPosition) {
        int startOfLine = cursorPosition;
        while (startOfLine > 0 && s.charAt(startOfLine - 1) != '\n') {
            startOfLine--;
        }
        return startOfLine;
    }

    public static int getEndOfLine(@NonNull CharSequence s, int cursorPosition) {
        int nextLinebreak = s.toString().indexOf('\n', cursorPosition);
        if (nextLinebreak > -1) {
            return nextLinebreak;
        }
        return cursorPosition;
    }
}

