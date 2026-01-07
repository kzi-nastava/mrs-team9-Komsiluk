import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { map, of } from 'rxjs';

export interface AddressSuggestion {
  label: string;
  lat: number;
  lon: number;
}

@Injectable({ providedIn: 'root' })
export class GeocodingService {
  constructor(private http: HttpClient) {}

  search(query: string) {
    const q = (query ?? '').trim();

    if (q.length < 3) {
      return of([] as AddressSuggestion[]);
    }

    const params = new HttpParams()
      .set('format', 'jsonv2')
      .set('q', `${q}, Novi Sad`)
      .set('limit', '15')
      .set('bounded', '1')
      .set('addressdetails', '1')
      .set('countrycodes', 'rs')
      .set('viewbox', '19.764,45.309,19.929,45.214');

    return this.http.get<any[]>('https://nominatim.openstreetmap.org/search', { params }).pipe(
      map(list => {
        const mapped = (list ?? []).map(x => ({
          label: this.formatLabel(x),
          lat: +x.lat,
          lon: +x.lon,
        } as AddressSuggestion));

        const normalize = (s: string) =>
          s.toLowerCase()
          .replace(/\s+/g, ' ')
          .replace(/[.,]/g, '')
          .trim();

        const seen = new Set<string>();
        const out: AddressSuggestion[] = [];

        for (const s of mapped) {
          const key = normalize(s.label);
          const key2 = `${s.lat.toFixed(6)},${s.lon.toFixed(6)}`;

          const combined = key + '|' + key2;
          if (seen.has(combined)) continue;

          if (seen.has(key)) continue;

          seen.add(combined);
          seen.add(key);
          out.push(s);
        }

        return out;
      })
    );
  }

  private formatLabel(r: any): string {
    const a = r.address ?? {};
    const parts: string[] = [];

    const road = a.road || a.pedestrian || a.footway || a.residential;
    const house = a.house_number;

    if (road) parts.push(house ? `${road} ${house}` : road);

    const neighbourhood = a.neighbourhood;
    if (neighbourhood) parts.push(neighbourhood);

    const suburb = a.suburb;
    if (suburb && suburb !== neighbourhood) parts.push(suburb);

    const cityDistrict = a.city_district;
    if (cityDistrict && cityDistrict !== suburb) parts.push(cityDistrict);

    const city = a.city || a.town || a.village;
    if (city) parts.push(city);

    if (parts.length === 0 && r.display_name) {
      return String(r.display_name).split(',').slice(0, 5).join(',').trim();
    }

    return Array.from(new Set(parts)).join(', ');
  }
}