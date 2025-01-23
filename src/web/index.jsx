import 'webpack-entry';

import { PluginManifest, PluginStore } from 'graylog-web-plugin/plugin';
import packageJson from '../../package.json';
import SamplePage from './pages/SamplePage'; // Adjusted import path

const manifest = new PluginManifest(packageJson, {
  routes: [
    {
      path: '/sample-page', // Define the URL for your page
      component: SamplePage, // Reference your SamplePage component
    },
  ],
  navigation: [
    {
      path: '/sample-page', // Add a link to this path in the navigation
      description: 'Sample Page', // Label for the navigation link
    },
  ],
});

PluginStore.register(manifest);
