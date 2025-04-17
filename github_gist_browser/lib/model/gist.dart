import 'gist_file.dart';

class Gist {
  final String? url;
  final String? forksUrl;
  final String? commitsUrl;
  final String? id;
  final String? nodeId;
  final String? gitPullUrl;
  final String? gitPushUrl;
  final String? htmlUrl;
  final Map<String, GistFile> files;
  final bool isPublic;
  final String? createdAt;
  final String? updatedAt;
  final String? description;
  final int comments;
  final String? commentsUrl;
  final bool truncated;

  Gist({
    required this.url,
    required this.forksUrl,
    required this.commitsUrl,
    required this.id,
    required this.nodeId,
    required this.gitPullUrl,
    required this.gitPushUrl,
    required this.htmlUrl,
    required this.files,
    required this.isPublic,
    required this.createdAt,
    required this.updatedAt,
    required this.description,
    required this.comments,
    required this.commentsUrl,
    required this.truncated,
  });

  factory Gist.fromJson(Map<String, dynamic> json) {
    final files = (json['files'] as Map<String, dynamic>).map(
          (key, value) => MapEntry(key, GistFile.fromJson(value)),
    );

    return Gist(
      url: json['url'],
      forksUrl: json['forks_url'],
      commitsUrl: json['commits_url'],
      id: json['id'],
      nodeId: json['node_id'],
      gitPullUrl: json['git_pull_url'],
      gitPushUrl: json['git_push_url'],
      htmlUrl: json['html_url'],
      files: files,
      isPublic: json['public'],
      createdAt: json['created_at'],
      updatedAt: json['updated_at'],
      description: json['description'],
      comments: json['comments'],
      commentsUrl: json['comments_url'],
      truncated: json['truncated'],
    );
  }
}
