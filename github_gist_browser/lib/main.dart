import 'dart:convert';

import 'package:flutter/material.dart';
import 'package:github_gist_browser/model/gist.dart';
import 'package:http/http.dart' as http;
import 'package:go_router/go_router.dart';
import 'package:url_launcher/url_launcher.dart';

final router = GoRouter(
  routes: [
    GoRoute(
      path: '/',
      builder: (context, state) => GistListScreen(username: ''),
    ),
    GoRoute(
      path: '/gists/:username',
      builder:
          (context, state) =>
              GistListScreen(username: state.pathParameters['username']!),
    ),
    // other routes
  ],
);

void main() => runApp(const MyApp());

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp.router(
      routerConfig: router,
      title: 'Github Gist Browser',
      theme: ThemeData(primarySwatch: Colors.blue),
    );
  }
}

class GistListScreen extends StatefulWidget {
  const GistListScreen({super.key, required this.username});

  final String username;

  @override
  State<GistListScreen> createState() => _GistListScreenState();
}

class _GistListScreenState extends State<GistListScreen> {
  List<Gist> _gists = [];
  bool _isLoading = false;
  String _errorMessage = '';

  @override
  void initState() {
    super.initState();
    _fetchGists(widget.username);
  }

  Future<void> _fetchGists(String username) async {
    if (username.isEmpty) {
      setState(() {
        _errorMessage = "Oh no, we couldn't get the username";
      });
      return;
    }

    setState(() {
      _isLoading = true;
      _errorMessage = '';
    });

    try {
      final response = await http.get(
        Uri.parse('https://api.github.com/users/$username/gists'),
        headers: {
          'Accept': 'application/vnd.github.v3+json',
          'X-GitHub-Api-Version': '2022-11-28',
        },
      );

      if (response.statusCode == 200) {
        final List body = json.decode(response.body);
        setState(() {
          _gists = body.map((e) => Gist.fromJson(e)).toList();
          _isLoading = false;
        });
      } else {
        setState(() {
          _errorMessage =
              'Something went wrong while fetching the gist list. Please try again.';
          _gists = [];
          _isLoading = false;
        });
      }
    } catch (e) {
      setState(() {
        _errorMessage =
            'Something went wrong while fetching the gist list. Please try again.';
        _gists = [];
        _isLoading = false;
      });
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        backgroundColor: Color(0xFF006A64),
        titleTextStyle: TextStyle(color: Colors.white, fontSize: 20),
        title: Text(
          widget.username.isNotEmpty
              ? "${widget.username}'s gists"
              : "Gist list",
        ),
      ),
      body: _buildBody()
    );
  }

  Widget _buildBody() {
    if (_isLoading) {
      return const Center(
        child: CircularProgressIndicator(color: Color(0xFF006A64)),
      );
    }

    if (_errorMessage.isNotEmpty) {
      return Padding(
        padding: const EdgeInsets.all(24.0),
        child: Center(
          child: Text(
            _errorMessage,
            textAlign: TextAlign.center,
            style: const TextStyle(
              fontSize: 24,
              fontWeight: FontWeight.bold,
              color: Color(0xFFFF8A80),
            ),
          ),
        ),
      );
    }

    if (_gists.isEmpty) {
      return const Center(
        child: Padding(
          padding: EdgeInsets.all(24.0),
          child: Text(
            'No gists to display',
            textAlign: TextAlign.center,
            style: TextStyle(
              fontSize: 36,
              fontWeight: FontWeight.bold,
              color: Colors.black12,
            ),
          ),
        ),
      );
    }

    // List of gists
    return ListView.separated(
      separatorBuilder: (context, index) => Divider(
        color: Color(0x13303030),
      ),
      itemCount: _gists.length,
      itemBuilder: (context, index) {
        final gist = _gists[index];
        final files = gist.files;
        final fileName = files.keys.first;
        final description = gist.description ?? 'No description';

        return Card(
          elevation: 0,
          color: Colors.transparent,
          child: ListTile(
            title: Text(fileName),
            subtitle: Text(
              description.isNotEmpty ? description : 'No description',
              maxLines: 2,
              overflow: TextOverflow.ellipsis,
            ),
            trailing: Text(
              'Files: ${files.length}',
              style: const TextStyle(fontWeight: FontWeight.bold),
            ),
            onTap: () async {
              await launchUrl(
                Uri.parse(gist.htmlUrl ?? ''),
                mode: LaunchMode.inAppBrowserView,
              );
            },
          ),
        );
      },
    );
  }
}
