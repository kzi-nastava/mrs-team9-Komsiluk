import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { BlockNoteCreateDTO, BlockNoteResponseDTO } from '../../../../../../shared/models/block-note.model';

@Injectable({ providedIn: 'root' })
export class BlockNoteService {
  private readonly API = 'http://localhost:8081/api/blocks';

  constructor(private http: HttpClient) {}

  create(dto: BlockNoteCreateDTO): Observable<BlockNoteResponseDTO> {
    return this.http.post<BlockNoteResponseDTO>(this.API, dto);
  }

  getLastForUser(userId: number): Observable<BlockNoteResponseDTO> {
    return this.http.get<BlockNoteResponseDTO>(`${this.API}/user/${userId}`);
  }
}