package com.komsiluk.taxi.ui.block;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.komsiluk.taxi.data.remote.block.BlockNoteCreateRequest;
import com.komsiluk.taxi.data.remote.block.BlockNoteResponse;
import com.komsiluk.taxi.data.remote.block.BlockService;
import com.komsiluk.taxi.data.remote.profile.UserService;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@HiltViewModel
public class AdminBlockUserViewModel extends ViewModel {

    private final BlockService blockApi;
    private final UserService userApi;

    private final MutableLiveData<AdminBlockUserState> state =
            new MutableLiveData<>(new AdminBlockUserState());

    private Call<List<String>> emailCall;
    private Call<BlockNoteResponse> blockCall;

    @Inject
    public AdminBlockUserViewModel(BlockService blockApi, UserService userApi) {
        this.blockApi = blockApi;
        this.userApi = userApi;
    }

    public LiveData<AdminBlockUserState> getState() {
        return state;
    }

    private void setLoading(boolean loading) {
        AdminBlockUserState s = state.getValue();
        if (s == null) s = new AdminBlockUserState();
        s.loading = loading;
        if (loading) s.error = null;
        state.setValue(s);
    }

    private void setError(String msg) {
        AdminBlockUserState s = state.getValue();
        if (s == null) s = new AdminBlockUserState();
        s.loading = false;
        s.error = msg;
        state.setValue(s);
    }

    public void clearSuccessFlag() {
        AdminBlockUserState s = state.getValue();
        if (s == null) s = new AdminBlockUserState();
        s.blockSuccess = false;
        state.setValue(s);
    }

    public void setEmailSuggestions(List<String> list) {
        AdminBlockUserState s = state.getValue();
        if (s == null) s = new AdminBlockUserState();
        s.emailSuggestions = list;
        state.setValue(s);
    }

    public void autocompleteEmails(String query, int limit) {
        cancelEmails();

        if (query == null) query = "";
        query = query.trim();

        if (query.length() < 3) {
            AdminBlockUserState s = state.getValue();
            if (s == null) s = new AdminBlockUserState();
            s.emailSuggestions = Collections.emptyList();
            state.setValue(s);
            return;
        }

        emailCall = userApi.autocompleteEmails(query, limit);
        emailCall.enqueue(new Callback<List<String>>() {
            @Override public void onResponse(Call<List<String>> call, Response<List<String>> resp) {
                if (!resp.isSuccessful() || resp.body() == null) {
                    AdminBlockUserState s = state.getValue();
                    if (s == null) s = new AdminBlockUserState();
                    s.emailSuggestions = Collections.emptyList();
                    state.setValue(s);
                    return;
                }
                AdminBlockUserState s = state.getValue();
                if (s == null) s = new AdminBlockUserState();
                s.emailSuggestions = resp.body();
                state.setValue(s);
            }

            @Override public void onFailure(Call<List<String>> call, Throwable t) {
                AdminBlockUserState s = state.getValue();
                if (s == null) s = new AdminBlockUserState();
                s.emailSuggestions = Collections.emptyList();
                state.setValue(s);
            }
        });
    }

    public void blockUser(String blockedEmail, long adminId, String reason) {
        cancelBlock();
        setLoading(true);

        BlockNoteCreateRequest req = new BlockNoteCreateRequest();
        req.setBlockedUserEmail(blockedEmail);
        req.setAdminId(adminId);
        req.setReason(reason);

        blockCall = blockApi.create(req);
        blockCall.enqueue(new Callback<BlockNoteResponse>() {
            @Override public void onResponse(Call<BlockNoteResponse> call, Response<BlockNoteResponse> resp) {
                if (!resp.isSuccessful() || resp.body() == null) {
                    setError("Block failed (" + resp.code() + ")");
                    return;
                }
                AdminBlockUserState s = state.getValue();
                if (s == null) s = new AdminBlockUserState();
                s.loading = false;
                s.error = null;
                s.blockSuccess = true;
                state.setValue(s);
            }

            @Override public void onFailure(Call<BlockNoteResponse> call, Throwable t) {
                setError("Network error: " + (t.getMessage() == null ? "unknown" : t.getMessage()));
            }
        });
    }

    private void cancelEmails() {
        if (emailCall != null) {
            emailCall.cancel();
            emailCall = null;
        }
    }

    private void cancelBlock() {
        if (blockCall != null) {
            blockCall.cancel();
            blockCall = null;
        }
    }
}
