package rs.ac.uns.ftn.iss.Komsiluk.services.interfaces;

import rs.ac.uns.ftn.iss.Komsiluk.dtos.block.BlockNoteCreateDTO;
import rs.ac.uns.ftn.iss.Komsiluk.dtos.block.BlockNoteResponseDTO;

public interface IBlockNoteService {

    public BlockNoteResponseDTO createBlock(BlockNoteCreateDTO dto);

    public BlockNoteResponseDTO getLastBlockForUser(Long userId);
}