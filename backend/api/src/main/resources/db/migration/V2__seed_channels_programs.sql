INSERT INTO channels (name, code, created_at) VALUES
  ('뉴스 채널', 'NEWS', NOW()),
  ('스포츠 채널', 'SPORTS', NOW()),
  ('예능 채널', 'ENTERTAINMENT', NOW()),
  ('교양 채널', 'CULTURE', NOW());

INSERT INTO programs (channel_id, name, code, created_at) VALUES
  (1, '아침 뉴스', 'MORNING_NEWS', NOW()),
  (1, '저녁 뉴스', 'EVENING_NEWS', NOW()),
  (1, '심야 뉴스', 'NIGHT_NEWS', NOW()),
  (2, '축구 생중계', 'LIVE_SOCCER', NOW()),
  (2, '야구 생중계', 'LIVE_BASEBALL', NOW()),
  (2, '스포츠 하이라이트', 'SPORTS_HIGHLIGHT', NOW()),
  (3, '예능 프로', 'VARIETY_SHOW', NOW()),
  (3, '드라마 A', 'DRAMA_A', NOW()),
  (3, '드라마 B', 'DRAMA_B', NOW()),
  (4, '다큐멘터리', 'DOCUMENTARY', NOW()),
  (4, '쿠킹 클래스', 'COOKING', NOW()),
  (4, '세계 여행', 'TRAVEL', NOW());
