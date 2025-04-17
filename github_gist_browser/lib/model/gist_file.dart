class GistFile {
  final String? filename;
  final String? type;
  final String? language;
  final String? rawUrl;
  final int size;

  GistFile({
    required this.filename,
    required this.type,
    required this.language,
    required this.rawUrl,
    required this.size,
  });

  factory GistFile.fromJson(Map<String, dynamic> json) {
    return GistFile(
      filename: json['filename'],
      type: json['type'],
      language: json['language'],
      rawUrl: json['raw_url'],
      size: json['size'],
    );
  }
}
