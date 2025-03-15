import 'webpack-entry';

import { PluginManifest, PluginStore } from 'graylog-web-plugin/plugin';
import packageJson from '../../package.json';
import AISearchPlugin from './pages/AISearchPlugin'; // Adjusted import path

const manifest = new PluginManifest(packageJson, {
  routes: [
    {
      path: '/ai-search-plugin', // Define the URL for your page
      component: AISearchPlugin, // Reference your AISearchPlugin component
    },
  ],
  navigation: [
    {
      path: '/ai-search-plugin', // Add a link to this path in the navigation
      description: 'AISearchPlugin', // Label for the navigation link
    },
  ],
});

PluginStore.register(manifest);
