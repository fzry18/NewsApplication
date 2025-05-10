# News Application

A modern Android news application that allows users to browse, search, and save news articles from various sources.

## Features

- **News Feed**: Browse the latest news articles from various sources
- **Search**: Find articles by keywords, topics, or sources
- **Categories**: Filter news by categories (e.g., Business, Technology, Sports)
- **Favorites**: Save articles to read later
- **Detailed View**: Read full articles with rich content formatting
- **Offline Support**: Access saved articles even when offline
- **Pull-to-refresh**: Update the news feed with the latest articles
- **Dark Theme Support**: Comfortable reading experience in all lighting conditions
- **Scroll-to-Top**: Quick navigation back to the top of the feed

## Architecture

This application follows the MVVM (Model-View-ViewModel) architecture pattern with Clean Architecture principles:

### Layers

- **Presentation Layer**: Activities, Fragments, Adapters, ViewModels
- **Domain Layer**: Use cases and business logic
- **Data Layer**: Repository implementations, data sources

### Components

- **View**: Activities and Fragments that display UI and handle user interactions
- **ViewModel**: Maintains UI state and handles user actions, communicating with repositories
- **Repository**: Provides an abstraction over the data sources (API and local database)
- **Data Sources**: Remote API and local Room database

### Key Patterns

- **Repository Pattern**: Abstracts data sources and provides a clean API to the rest of the application
- **Dependency Injection**: Uses manual injection (via Injection class) to provide dependencies
- **Observer Pattern**: LiveData for reactive UI updates
- **Factory Pattern**: Creates ViewModels with required dependencies

## Tech Stack

- **Kotlin**: Primary programming language
- **Coroutines**: For asynchronous programming
- **LiveData**: For observable data holder pattern
- **ViewModel**: To manage UI-related data in a lifecycle-conscious way
- **Room**: For local database storage of favorite articles
- **Retrofit**: For API communication
- **Glide**: For image loading and caching
- **Material Components**: For modern, consistent UI
- **ViewBinding**: For type-safe view access
- **RecyclerView**: For efficient list display

## API

The application uses the [News API](https://newsapi.org/) to fetch articles from various sources. Features used from the API:

- Top Headlines
- Search functionality
- Source filtering
- Category filtering

## Setup Instructions

1. Clone the repository
2. Create a `local.properties` file in the project root with the following:NEWS_API_KEY=your_api_key_here | BASE_URL=https://newsapi.org/v2/
3. Get your API key from [News API](https://newsapi.org/)
4. Build and run the application

## Screenshots

[Screenshots would be added here]

## Future Improvements

- Add user authentication
- Implement news categorization
- Add article sharing functionality
- Support for multiple languages
- Improved offline caching
- Push notifications for breaking news
