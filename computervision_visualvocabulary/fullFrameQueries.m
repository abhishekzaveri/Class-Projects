addpath('./provided_code/');
load('kMeans.mat');
directoryframes = 'frames/';
directorysift = 'sift/';
allsift = dir([directorysift '/*.mat']); 
qrs = allsift(randperm(1000, 3)); % find 3 random frames to use as queries
for i = 1:3 % create histogram for three random queries
    qr = [directorysift '/' qrs(i).name];
    load(qr, 'imname', 'descriptors');
    framename(i,:) = imname; % store imnames of queries 
    distances = dist2(clustercenters, descriptors); % find min euc distances between words and descriptors for histogram
    [~,x] = min(distances);
    histogram = histc(x, 1:length(clustercenters)); 
    hists1(i,:) = histogram/norm(histogram); % normalize histogram
end
for i = 1:length(allsift) % create histogram for all frames in directorysift
    qr = [directorysift '/' allsift(i).name];
    load(qr, 'imname', 'descriptors');
    N(i, :) = imname; % store all imnames
    distances = dist2(clustercenters, descriptors); % find min euc distances between words and descriptors for histogram
    [~,x] = min(distances);
    histogram = histc(x, 1:length(clustercenters)); 
    hists2(i,:) = histogram/norm(histogram); % normalize histogram
end
products = hists1*(hists2'); % find normalized scalar product using 2 histograms 
products(isnan(products)) = 0; % remove any NaNs to avoid black frames
[~,msframes] = sort(products, 2, 'descend'); % sort according to most similar in rank order 
a = 1;
for j = 1:3 % imshow frame and M = 5 similar frames 
    displayframes = framename(j,:);
    subplot(3, 6, a);
    x1 = [directoryframes '/' displayframes]; 
    imshow(x1);
    a = a + 1;
    for k = 1:5
        displayM5frames = N(msframes(j,k),:);
        subplot(3, 6, a);
        x2 = [directoryframes '/' displayM5frames]; 
        imshow(x2);
        a = a + 1;
    end
end