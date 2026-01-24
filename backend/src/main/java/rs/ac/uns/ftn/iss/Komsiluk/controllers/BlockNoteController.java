package rs.ac.uns.ftn.iss.Komsiluk.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import rs.ac.uns.ftn.iss.Komsiluk.dtos.block.BlockNoteCreateDTO;
import rs.ac.uns.ftn.iss.Komsiluk.dtos.block.BlockNoteResponseDTO;
import rs.ac.uns.ftn.iss.Komsiluk.services.interfaces.IBlockNoteService;

@RestController
@RequestMapping("/api/blocks")
public class BlockNoteController {

	@Autowired
    private IBlockNoteService blockNoteService;

	@PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<BlockNoteResponseDTO> create(@Valid @RequestBody BlockNoteCreateDTO dto) {
        BlockNoteResponseDTO created = blockNoteService.createBlock(dto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

	@PreAuthorize("hasAnyRole('DRIVER', 'PASSENGER')")
    @GetMapping("/user/{userId}")
    public ResponseEntity<BlockNoteResponseDTO> getForUser(@PathVariable Long userId) {
        BlockNoteResponseDTO responseDTO = blockNoteService.getLastBlockForUser(userId);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }
}