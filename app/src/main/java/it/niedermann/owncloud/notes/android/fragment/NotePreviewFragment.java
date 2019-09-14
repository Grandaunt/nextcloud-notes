package it.niedermann.owncloud.notes.android.fragment;

import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.yydcdut.markdown.syntax.text.TextFactory;
import com.yydcdut.rxmarkdown.RxMDTextView;
import com.yydcdut.rxmarkdown.RxMarkdown;

import androidx.annotation.Nullable;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import butterknife.BindView;
import butterknife.ButterKnife;
import it.niedermann.owncloud.notes.R;
import it.niedermann.owncloud.notes.persistence.NoteSQLiteOpenHelper;
import it.niedermann.owncloud.notes.util.ICallback;
import it.niedermann.owncloud.notes.util.MarkDownUtil;
import it.niedermann.owncloud.notes.util.NotesClientUtil;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class NotePreviewFragment extends BaseNoteFragment {

    private NoteSQLiteOpenHelper db = null;

    @BindView(R.id.swiperefreshlayout)
    SwipeRefreshLayout swipeRefreshLayout;

    @BindView(R.id.single_note_content)
    RxMDTextView noteContent;

    public static NotePreviewFragment newInstance(long noteId) {
        NotePreviewFragment f = new NotePreviewFragment();
        Bundle b = new Bundle();
        b.putLong(PARAM_NOTE_ID, noteId);
        f.setArguments(b);
        return f;
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.findItem(R.id.menu_edit).setVisible(true);
        menu.findItem(R.id.menu_preview).setVisible(false);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_single_note, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ButterKnife.bind(this, getView());

        setActiveTextView(noteContent);

        String content = note.getContent();

        RxMarkdown.with(content, getActivity())
                .config(
                        MarkDownUtil.getMarkDownConfiguration(noteContent.getContext())
                                /*.setOnTodoClickCallback(new OnTodoClickCallback() {
                                        @Override
                                        public CharSequence onTodoClicked(View view, String line, int lineNumber) {
                                        String[] lines = TextUtils.split(note.getContent(), "\\r?\\n");
                                        if(lines.length >= lineNumber) {
                                            lines[lineNumber] = line;
                                        }
                                        noteContent.setText(TextUtils.join("\n", lines), TextView.BufferType.SPANNABLE);
                                        saveNote(null);
                                        return line;
                                    }
                                }
                            )*/.build()
                )
                .factory(TextFactory.create())
                .intoObservable()
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<CharSequence>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.v(getClass().getSimpleName(), "RxMarkdown error", e);
                    }

                    @Override
                    public void onNext(CharSequence charSequence) {
                        noteContent.setText(charSequence, TextView.BufferType.SPANNABLE);
                    }
                });
        noteContent.setText(content);
        noteContent.setMovementMethod(LinkMovementMethod.getInstance());

        db = NoteSQLiteOpenHelper.getInstance(getActivity().getApplicationContext());
        // Pull to Refresh
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (db.getNoteServerSyncHelper().isSyncPossible()) {
                    swipeRefreshLayout.setRefreshing(true);
                    db.getNoteServerSyncHelper().addCallbackPull( new ICallback() {
                        @Override
                        public void onFinish() {
                            noteContent.setText(db.getNote(note.getId()).getContent(), TextView.BufferType.SPANNABLE);
                            swipeRefreshLayout.setRefreshing(false);
                        }

                        @Override
                        public void onScheduled() {
                        }
                    });
                    db.getNoteServerSyncHelper().scheduleSync(false);
                } else {
                    swipeRefreshLayout.setRefreshing(false);
                    Toast.makeText(getActivity().getApplicationContext(), getString(R.string.error_sync, getString(NotesClientUtil.LoginStatus.NO_NETWORK.str)), Toast.LENGTH_LONG).show();
                }
            }
        });

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
        noteContent.setTextSize(TypedValue.COMPLEX_UNIT_PX, getFontSizeFromPreferences(sp));
        if (sp.getBoolean(getString(R.string.pref_key_font), false)) {
            noteContent.setTypeface(Typeface.MONOSPACE);
        }
    }

    @Override
    protected String getContent() {
        return note.getContent();
    }
}
