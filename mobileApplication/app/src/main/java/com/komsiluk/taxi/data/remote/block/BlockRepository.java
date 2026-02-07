package com.komsiluk.taxi.data.remote.block;

import javax.inject.Inject;

import retrofit2.Call;

public class BlockRepository {

    private final BlockService api;

    @Inject
    public BlockRepository(BlockService api) {
        this.api = api;
    }

    public Call<BlockNoteResponse> create(String email, long adminId, String reason) {
        BlockNoteCreateRequest req = new BlockNoteCreateRequest();
        req.setBlockedUserEmail(email);
        req.setAdminId(adminId);
        req.setReason(reason);
        return api.create(req);
    }

    public Call<BlockNoteResponse> getForUser(long userId) {
        return api.getForUser(userId);
    }
}
