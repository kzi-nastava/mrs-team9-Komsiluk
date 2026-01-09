package rs.ac.uns.ftn.iss.Komsiluk.mappers;

import org.springframework.stereotype.Component;

import rs.ac.uns.ftn.iss.Komsiluk.beans.BlockNote;
import rs.ac.uns.ftn.iss.Komsiluk.dtos.block.BlockNoteResponseDTO;

@Component
public class BlockNoteDTOMapper {

	public BlockNoteResponseDTO toResponseDTO(BlockNote node) {
        BlockNoteResponseDTO dto = new BlockNoteResponseDTO();
        dto.setId(node.getId());
        dto.setReason(node.getReason());
        dto.setCreatedAt(node.getCreatedAt());

        if (node.getBlockedUser() != null) {
            dto.setBlockedUserEmail(node.getBlockedUser().getEmail());
        }

        if (node.getAdmin() != null) {
            dto.setAdminEmail(node.getAdmin().getEmail());
        }

        return dto;
    }
}
