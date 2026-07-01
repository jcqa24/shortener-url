export interface ShortenResponse {
  shortCode: string;
  shortUrl: string;
  originalUrl: string;
  permanent: boolean;
  expiresAt: string | null;
}

export interface UrlHistoryItem {
  id: number;
  shortCode: string;
  shortUrl: string;
  originalUrl: string;
  createdAt: string;
  clickCount: number;
}